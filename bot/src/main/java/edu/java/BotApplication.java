package edu.java;

import edu.java.configuration.ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ClientConfiguration.class})
public class BotApplication {
    private static final Logger logger = LoggerFactory.getLogger(BotApplication.class);
    public static void main(String[] args) {
        logger.info(System.getenv("APP_TELEGRAM_TOKEN"));
        SpringApplication.run(BotApplication.class, args);
    }

}
