package ru.otus.msa.notification;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.otus.msa.notification.adapter.out.pg.entity.NotificationEntity;
import ru.otus.msa.notification.adapter.out.pg.repository.NotificationEntityRepository;
import ru.otus.msa.notification.application.MessageProcessor;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.kafka.OrderEvent;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.otus.msa.notification.KafkaTestUtil.createKafkaProducer;
import static ru.otus.msa.order.api.common.OrderStatus.*;

@Log4j2
public class NotificationServiceTest extends BaseContainerTest {

    @Autowired
    private NotificationEntityRepository notificationEntityRepository;

    @Autowired
    private MessageProcessor messageProcessor;

    @DisplayName("Уведомление создано")
    @ParameterizedTest
    @MethodSource("orderStatusProvider")
    void orderCreatedTest(OrderStatus status) {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(orderId)
                .status(status)
                .orderName("Заказ 1")
                .rentStartDate("01.01.2026")
                .rentCompleteDate("02.01.2026")
                .pickUpAddress("Самара, Пятая просека 110Б")
                .pickUpTimes("10-20:00")
                .userId(userId)
                .managerId(managerId)
                .items(List.of())
                .build();
        try (KafkaProducer<String, OrderEvent> producer = createKafkaProducer(kafkaContainer.getBootstrapServers())) {
            producer.send(new ProducerRecord<>(ORDER_EVENT_TOPIC, orderEvent));
        }

        Awaitility.await()
                .atMost(Duration.of(20, ChronoUnit.SECONDS))
                .untilAsserted(() -> {
                    List<NotificationEntity> all = notificationEntityRepository.findAll();
                    assertThat(all.size()).isEqualTo(1);
                    String expectedContent = messageProcessor.getContent(orderEvent);
                    String actualContent = all.getFirst().getContent();
                    log.info("actualContent {}", actualContent);
                    assertThat(actualContent).isEqualTo(expectedContent);
                });
    }

    @BeforeEach
    void setUp() {
        notificationEntityRepository.deleteAll();
    }

    static Stream<OrderStatus> orderStatusProvider() {
        return Stream.of(CREATED, WAITING_CLIENT_RECEIVE,
                WAITING_ADMIN_RECEIVE, COMPLETED,
                REJECTED
        );
    }
}
