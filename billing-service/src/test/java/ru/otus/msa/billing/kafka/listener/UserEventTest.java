package ru.otus.msa.billing.kafka.listener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.Duration;
import java.util.UUID;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import ru.otus.msa.billing.BaseContainerTest;
import ru.otus.msa.billing.adapter.out.pg.repository.AccountEntityRepository;
import ru.otus.msa.user.api.kafka.dto.UserEvent;
import ru.otus.msa.user.api.kafka.dto.UserEventStatus;

public class UserEventTest extends BaseContainerTest {

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Test
    @DisplayName("Событие о создании пользователя успешно обработано")
    void userCreatedEventTest() {
        UUID userId = UUID.randomUUID();
        try (KafkaProducer<String, UserEvent> producer = KafkaTestUtil.createKafkaProducer(kafka.getBootstrapServers())) {
            UserEvent userEvent = UserEvent.builder()
                    .userId(userId)
                    .gender(false)
                    .firstName("Иван")
                    .lastName("Филипов")
                    .status(UserEventStatus.CREATED)
                    .build();
            producer.send(new ProducerRecord<>(USER_EVENT_TOPIC, userEvent));

        }
        Awaitility.await().atMost(Duration.ofSeconds(20))
                .untilAsserted(() -> {
                    assertThat(accountEntityRepository.findByUserId(userId).size())
                            .isEqualTo(2);
                });
    }
}
