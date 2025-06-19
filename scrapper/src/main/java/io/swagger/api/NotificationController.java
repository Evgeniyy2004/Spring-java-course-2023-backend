package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.services.JdbcNotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class NotificationController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcNotificationService notificationService;

    @org.springframework.beans.factory.annotation.Autowired
    public NotificationController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping("/{id}/mailing")
    @Valid
    public ResponseEntity mailingIdPost(
        @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable
        Integer id, @RequestParam("do") Integer mailing
    ) {
        notificationService.changeMailing(id, mailing);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/{id}/mailing")
    @Valid
    public ResponseEntity<Boolean> mailingIdGet(
        @PathVariable("id")
        Integer id
    ) {
        return ResponseEntity.ok(notificationService.getMailing(id));
    }

    @PostMapping("/{id}/group")
    @Valid
    public ResponseEntity groupIdPost(
        @PathVariable("id")
        Integer id, @RequestParam("new") String group
    ) {
        notificationService.add(id, group);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/group")
    @Valid
    public ResponseEntity<Boolean> groupIdCheck(
        @PathVariable("id")
        Integer id
    ) {

        return ResponseEntity.ok(notificationService.check(id));
    }

    @PostMapping("/{id}/notify")
    public ResponseEntity notifyIdPost(
        @PathVariable("id")
        Integer id, @RequestParam("new") boolean notify
    ) {
        notificationService.changeNotificationStatus(id, notify);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/notify")
    public ResponseEntity<Boolean> notifyIdGet(@PathVariable("id")
        Integer id
    ) {
        return ResponseEntity.ok(notificationService.getNotification(id));
    }

}
