package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
                            date = "2024-02-27T16:17:37.541889551Z[GMT]")
@RestController
public class
TgChatApiController implements TgChatApi {

    private static final Logger LOG = LoggerFactory.getLogger(TgChatApiController.class);

    private static final String HEADER = "Accept";

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public TgChatApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> tgChatIdDelete(
        @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id")
        Long id
    ) {
        String accept = request.getHeader(HEADER);
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity tgChatIdPost(
        @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id")
        Long id
    ) {
        String accept = request.getHeader(HEADER);
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

}