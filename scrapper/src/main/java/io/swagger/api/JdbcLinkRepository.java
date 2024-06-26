package io.swagger.api;

import edu.java.model.ApiException;
import edu.java.siteclients.GitHubClient;
import edu.java.siteclients.StackOverflowClient;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Repository
@SuppressWarnings("all")
public class JdbcLinkRepository implements LinkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private GitHubClient git;

    @Autowired
    private StackOverflowClient stack;

    @Autowired
    public JdbcLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long id, String link) throws ApiException {
        String query = ("select * from id where id=?");
        var first = jdbcTemplate.queryForList(query, id).toArray();
        if (first.length == 0) {
            throw new ApiException(404, "Чат не существует");
        }
        var time = new Timestamp(System.currentTimeMillis());
        var check = jdbcTemplate.queryForList("select id from connect where link=? and id=?", link, id).toArray();
        if (check.length > 0) {
            throw new ApiException(400, "Ссылка уже добавлена");
        }
        var action = "insert into connect  values (?, ?, ?)";
        jdbcTemplate.update(action, link, id, time);

    }

    public void remove(Long id, String link) throws ApiException {
        String query = ("select * from id where id=?");
        var doo = jdbcTemplate.queryForList(query, id).toArray();
        if (doo.length == 0) {
            throw new ApiException(404, "Чат не существует");
        }
        var check = jdbcTemplate.queryForList("select id from connect where link=? and id=?", link, id).toArray();
        if (check.length == 0) {
            throw new ApiException(409, "Ссылка не отслеживается");
        }
        jdbcTemplate.update("delete from connect where link =? and id = ?", link, id);
    }

    public Collection<URI> findAll(Long id) throws ApiException {
        try {
            String query = ("select * from id where id=?");
            var doo = jdbcTemplate.queryForList(query, id).toArray();
            if (doo.length == 0) {
                throw new ApiException(404, "Чат не существует");
            }
            var res =
                jdbcTemplate.queryForList("select link from connect where id=" + id + ";", String.class).toArray();
            var result = new URI[res.length];
            for (int i = 0; i < res.length; i++) {
                result[i] = new URI(res[i].toString());
            }
            return List.of(result);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<Long, Collection<String>> update() {
        var time = new Timestamp(System.currentTimeMillis() - 3600000);
        var now = new Timestamp(System.currentTimeMillis());
        var res =
            jdbcTemplate.queryForList("select (id,link,updated) from connect where updated<?", time);
        jdbcTemplate.update("update connect set updated=? where updated<?", now, time);
        HashMap<Long, Collection<String>> result = new HashMap<>();
        for (Map i : res) {
            var current = i.get("link").toString();
            var id = Long.parseLong(i.get("id").toString());
            if (current.startsWith("https://stackoverflow.com/questions/")) {
                current = current.replace("https://stackoverflow.com/questions/", "");
                var question = Long.parseLong(Arrays.stream(current.split("/")).filter(x -> !x.equals(""))
                    .toArray(x -> new String[x])[0]);
                try {
                    var response = stack.fetchQuestion(question);

                    if (Timestamp.valueOf(response.time.toLocalDateTime()).after((Timestamp) i.get("updated"))) {
                        if (!result.containsKey(id)) {
                            result.put(id, new HashSet<>());
                        }
                        result.get(id).add(i.get("link").toString());
                    }
                } catch (WebClientResponseException e) {
                    continue;
                }
            } else {
                current = current.replace("https://github.com/", "");
                var repoAuthor =
                    Arrays.stream(current.split("/")).filter(x -> !x.equals("")).toArray(x -> new String[x]);
                try {
                    var response = git.fetchRepository(repoAuthor[0], repoAuthor[1]);
                    if (Timestamp.valueOf(response.time.toLocalDateTime()).after((Timestamp) i.get("updated"))) {
                        if (!result.containsKey(id)) {
                            result.put(id, new HashSet<>());
                        }
                        result.get(id).add(i.get("link").toString());
                    }
                } catch (WebClientResponseException e) {
                    continue;
                }
            }
        }
        return result;
    }

}
