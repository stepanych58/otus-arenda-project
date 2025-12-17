package ru.otus.msa.billing;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

import lombok.extern.log4j.Log4j2;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Log4j2
public class BaseContainerTest {

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine")
                    .withReuse(true);

    @Container
    protected final static KafkaContainer kafka = new KafkaContainer("apache/kafka-native:3.8.0")
            .withReuse(true);

    public static final String ORDER_EVENT_TOPIC = "order-event";

    public static final String USER_EVENT_TOPIC = "user-event";

    public static final String BILLING_ORDER_EVENT_TOPIC = "billing-order-event";

    @BeforeAll
    static void createTopic() {
        try (AdminClient client = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()))) {
            Collection<NewTopic> topics = List.of(
                    new NewTopic(ORDER_EVENT_TOPIC, 1, (short)1),
                    new NewTopic(BILLING_ORDER_EVENT_TOPIC, 1, (short)1),
                    new NewTopic(USER_EVENT_TOPIC, 1, (short)1)
            );
            client.createTopics(topics);
        } catch (Exception e) {
            log.error(e);
        }
    }

    @DynamicPropertySource
    static void initPostgresProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @DynamicPropertySource
    static void initKafkaProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}
