package edu.java.scrapperclient;


import edu.java.model.ClassResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import java.util.List;

@HttpExchange("/schedule/{id}/{time}")
public interface ScrapperScheduleClient {

    @GetExchange
    ResponseEntity<List<ClassResponse>> get(@PathVariable Long id, @PathVariable String time);

}
