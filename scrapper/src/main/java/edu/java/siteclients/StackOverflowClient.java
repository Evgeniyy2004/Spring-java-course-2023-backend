package edu.java.siteclients;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

@HttpExchange(url = "2.3/questions/{id}?site=stackoverflow&filter=withbody", accept = APPLICATION_JSON)
public interface StackOverflowClient {
    @GetExchange
    QuestionResponse fetchQuestion(@PathVariable long id);
}
