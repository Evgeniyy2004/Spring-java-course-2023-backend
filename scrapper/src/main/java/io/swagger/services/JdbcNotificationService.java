package io.swagger.services;

import io.swagger.api.JdbcNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JdbcNotificationService {
    private final JdbcNotificationRepository repo;

    @Autowired
    public JdbcNotificationService(JdbcNotificationRepository repo) {
        this.repo = repo;
    }

    public void add(long tgChatId, String group) {
        repo.save(tgChatId, group);
    }

    public void changeNotificationStatus(long tgChatId, boolean notify) {
        repo.updateNotifications(tgChatId, notify);
    }

    public boolean getNotification(long tgChatId){
        return repo.getNotification(tgChatId);
    }

    public void changeMailing(long tgChatId, Integer newhour) {
        repo.updateMailing(tgChatId, newhour);
    }

    public boolean getMailing(long tgChatId){
       return repo.getMailing(tgChatId);
    }

    public boolean check(long tgChatId) {
        return repo.check(tgChatId);
    }

}
