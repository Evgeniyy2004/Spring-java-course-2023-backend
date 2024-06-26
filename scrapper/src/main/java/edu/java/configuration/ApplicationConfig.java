package edu.java.configuration;

import edu.java.botclient.UpdatesClient;
import edu.java.siteclients.GitHubClient;
import edu.java.siteclients.StackOverflowClient;
import io.swagger.api.JdbcLinkRepository;
import io.swagger.api.ScrapperQueueProducer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;
import javax.sql.DataSource;
import listener.LinkUpdaterScheduler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@ComponentScan(basePackages = "io.swagger")
@Configuration
@EnableScheduling
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public class ApplicationConfig {
    private static final String BASE = "http://localhost:8081/";

    @Value("${app.codes}")
    public ArrayList<Integer> codes;

    @Value("${app.topic}")
    @Getter
    private static String topic;

    @Value("${app.use-queue}")
    @Getter
    private static boolean useQueue;

    @Value("${app.strategy}")
    STRATEGY strategy;

    public enum STRATEGY {
        CONSTANT,
        EXPONENTIAL
    }

    @Bean
    public LinkUpdaterScheduler scheduler(UpdatesClient client, ScrapperQueueProducer queue, JdbcLinkRepository repo) {
        return new LinkUpdaterScheduler(client, queue, repo);
    }

    @Bean
    public StackOverflowClient beanStack1() {
        WebClient restClient =
            WebClient.builder().baseUrl("https://api.stackexchange.com/").filter(withRetryableRequests()).build();
        WebClientAdapter adapter = WebClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(StackOverflowClient.class);
    }

    @Bean
    public GitHubClient beanGit1() {
        WebClient restClient =
            WebClient.builder().baseUrl("https://api.github.com/").filter(withRetryableRequests()).build();
        WebClientAdapter adapter = WebClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(GitHubClient.class);
    }

    private ExchangeFilterFunction withRetryableRequests() {
        if (strategy == STRATEGY.EXPONENTIAL) {
            return (request, next) -> next.exchange(request)
                .flatMap(clientResponse -> Mono.just(clientResponse)
                    .filter(response -> codes.contains(response.statusCode().value()))
                    .flatMap(response -> clientResponse.createException())
                    .flatMap(Mono::error)
                    .thenReturn(clientResponse))
                .retryWhen(this.retryBackoffExp());
        } else {
            return (request, next) -> next.exchange(request)
                .flatMap(clientResponse -> Mono.just(clientResponse)
                    .filter(response -> codes.contains(response.statusCode().value()))
                    .flatMap(response -> clientResponse.createException())
                    .flatMap(Mono::error)
                    .thenReturn(clientResponse))
                .retryWhen(this.retryConst());
        }
    }

    private RetryBackoffSpec retryBackoffExp() {
        return Retry.backoff(2 + 1, Duration.ofSeconds(2))
            .filter(throwable -> throwable instanceof WebClientResponseException)
            // here filter on the errors for which you want a retry
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
    }

    private RetryBackoffSpec retryConst() {
        return (Retry.fixedDelay(2 + 1, Duration.ofSeconds(2)))
            .filter(throwable -> throwable instanceof WebClientResponseException)
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
    }

    @Bean
    public UpdatesClient beanUpdates() {
        WebClient restClient = WebClient.builder().baseUrl(BASE).filter(withRetryableRequests()).build();
        WebClientAdapter adapter = WebClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(UpdatesClient.class);
    }

    /*@Bean
    public DataSource dataSource() {
        var namePassword = "postgres";
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5433/scrapper");
        dataSource.setUsername(namePassword);
        dataSource.setPassword(namePassword);
        return dataSource;
    }*/

    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory(DataSource source) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(source);
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(DataSource source) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory(source).getObject());
        return txManager;
    }

    @Bean
    public Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(
            "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return hibernateProperties;
    }

    @Bean
    public JdbcTemplate template(DataSource source) {
        return new JdbcTemplate(source);
    }
}
