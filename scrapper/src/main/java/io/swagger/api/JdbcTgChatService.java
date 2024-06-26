package io.swagger.api;

import edu.java.model.ApiException;
import io.swagger.services.TgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JdbcTgChatService implements TgChatService {


    private final JdbcTgChatRepository repo;


    @Autowired
    public JdbcTgChatService(JdbcTgChatRepository repo) {
        this.repo = repo;
    }

    @Override
    public void register(long tgChatId) throws ApiException {
        repo.save(tgChatId);
    }

    @Override
    public void unregister(long tgChatId) throws ApiException {
        repo.remove(tgChatId);
    }
}
