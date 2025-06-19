package edu.java.configuration;

import edu.java.scrapperclient.ScrapperNotificationClient;
import edu.java.scrapperclient.ScrapperScheduleClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@Configuration
@Validated
@ComponentScan(basePackages = "io.swagger.api")
@SuppressWarnings("RegexpSinglelineJava")
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "app1", ignoreUnknownFields = false)
public class ClientConfiguration {
    String base = "http://scheduleservice:8080/";


    @Bean
    public ScrapperNotificationClient beanChat() {
        WebClient restClient = WebClient.builder().baseUrl(base).filter(withRetryableRequests()).build();
        WebClientAdapter adapter = WebClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ScrapperNotificationClient.class);
    }

    @Bean
    public ScrapperScheduleClient beanLinks() {
        WebClient restClient = WebClient.builder().baseUrl(base).filter(withRetryableRequests()).build();
        WebClientAdapter adapter = WebClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ScrapperScheduleClient.class);
    }

    private ExchangeFilterFunction withRetryableRequests() {
            HashSet<Integer> codes = new HashSet<>();
            codes.add(429);
            codes.add(503);
            return (request, next) -> next.exchange(request)
                .flatMap(clientResponse -> Mono.just(clientResponse)
                    .filter(response -> codes.contains(response.statusCode().value()))
                    .flatMap(response -> clientResponse.createException())
                    .flatMap(Mono::error)
                    .thenReturn(clientResponse))
                .retryWhen(this.retryConst());
    }


    private RetryBackoffSpec retryConst() {
        return (Retry.fixedDelay(2 + 1, Duration.ofSeconds(2 + 2 + 1)))
            .filter(throwable -> throwable instanceof WebClientResponseException)
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
    }

}
