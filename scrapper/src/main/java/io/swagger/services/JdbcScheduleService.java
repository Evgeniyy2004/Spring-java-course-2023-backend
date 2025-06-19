package io.swagger.services;

import edu.java.model.ClassResponse;
import io.swagger.api.JdbcScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JdbcScheduleService{


    private final JdbcScheduleRepository repo;


    @Autowired
    public JdbcScheduleService(JdbcScheduleRepository repo) {
        this.repo = repo;
    }


    public List<ClassResponse> getSchedule(int tgChatId, String timediff) {
        return repo.findAll(tgChatId, timediff);
    }

}
