package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.services.JdbcScheduleService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ComponentScan(basePackages = "io.swagger")
public class ScheduleApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleApiController.class);
    private final JdbcScheduleService scheduleService;
    private final ObjectMapper objectMapper;
    ;

    @org.springframework.beans.factory.annotation.Autowired
    public ScheduleApiController(ObjectMapper objectMapper, JdbcScheduleService service) {
        this.scheduleService = service;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/schedule/{id}/{time}")
    public ResponseEntity<?> schedulesGet(
        @PathVariable("id") Integer tgChatId, @PathVariable("time") String time
    ) {
        var res = scheduleService.getSchedule(tgChatId, time);
        return ResponseEntity.ok(res);
    }

}
