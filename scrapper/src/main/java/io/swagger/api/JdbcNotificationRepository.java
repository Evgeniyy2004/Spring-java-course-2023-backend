package io.swagger.api;

import edu.java.model.ApiException;
import edu.java.model.ClassResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class JdbcNotificationRepository {
    private static final String QUERY = "select * from Student where Id=?";
    private static final String GROUPCHECK = "select * from Groups where GroupName=?";
    private static final String GROUPUPDATE = "insert into Groups (GroupName) values (?)";
    private final JdbcTemplate jdbcTemplate;
    private final JdbcScheduleRepository scheduleRepository;

    @Autowired
    public JdbcNotificationRepository(JdbcTemplate jdbcTemplate, JdbcScheduleRepository scheduleRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.scheduleRepository = scheduleRepository;
    }

    public void save(Long id, String group) {
        var res = jdbcTemplate.queryForList(GROUPCHECK, group);
        var res1 = jdbcTemplate.queryForList(QUERY, id);
        if (res.isEmpty()) {
           jdbcTemplate.update(GROUPUPDATE, group);
            var addedClasses = scheduleRepository.update();
            scheduleRepository.updateAllClasses(new HashMap<>(), addedClasses.getRight());
        }
        var groupid = jdbcTemplate.queryForObject("select Id from Groups where GroupName=?",
            Integer.class, group);
        if (res1.isEmpty()) {
            jdbcTemplate.update("INSERT INTO Student " +
                "VALUES (?,?,?,?)", id, groupid,false,null);

        } else {
            jdbcTemplate.update("Update Student set GroupId=? where Id=?", groupid, id);
        }
    }

    public void updateNotifications(Long id, boolean notification) {
        jdbcTemplate.update("Update Student set IsNotified=? where Id=?", notification, id);
    }

    public Boolean getNotification(Long id) {
        return jdbcTemplate.queryForObject
            ("Select IsNotified from Student where Id="+id,Boolean.class);
    }

    public void updateMailing(Long id, Integer mailing) {
        jdbcTemplate.update("Update Student set MailingTime=? where Id=?", mailing, id);
    }

    public Boolean getMailing(Long id) {
        return jdbcTemplate.queryForObject
            ("Select MailingTime from Student where Id="+id,Integer.class)!=null;
    }

    public boolean check(Long id){
        return !jdbcTemplate.queryForList("Select * from Student where Id=?", id).isEmpty();
    }
}
