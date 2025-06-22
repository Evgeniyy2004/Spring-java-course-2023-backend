package io.swagger.api;

import edu.java.configuration.ApplicationConfig;
import edu.java.model.LinkUpdate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ScrapperQueueProducer {

    private final KafkaTemplate<Integer, String> template;

    public ScrapperQueueProducer(KafkaTemplate<Integer, String> template) {
        this.template = template;
    }

    public void send(LinkUpdate update) {
        template.send(ApplicationConfig.getTopic(), update.toString());
    }
}
