package ru.otus.msa.billing.kafka.listener;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.otus.msa.billing.BaseContainerTest;
import ru.otus.msa.billing.adapter.out.pg.entity.BillingOrderOutboxEntity;
import ru.otus.msa.billing.adapter.out.pg.repository.AccountEntityRepository;
import ru.otus.msa.billing.adapter.out.pg.repository.BillingOrderOutboxRepository;
import ru.otus.msa.billing.adapter.out.pg.repository.PaymentRepository;
import ru.otus.msa.billing.application.BillingService;
import ru.otus.msa.billing.domain.Account;
import ru.otus.msa.order.api.http.CurrencyEnumDto;
import ru.otus.msa.order.api.kafka.OrderEvent;
import ru.otus.msa.order.api.kafka.OrderItemEvent;
import ru.otus.msa.user.api.BillingEventStatus;
import ru.otus.msa.user.api.kafka.dto.UserEvent;
import ru.otus.msa.user.api.kafka.dto.UserEventStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.otus.msa.order.api.common.OrderStatus.WAITING_PAYMENT_DEPOSIT;

@Log4j2
public class OrderDepositTest extends BaseContainerTest {

    @Autowired
    private BillingService billingService;

    @Autowired
    private BillingOrderOutboxRepository outboxRepository;

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void init() {
        outboxRepository.deleteAll();
        accountEntityRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    @DisplayName("На счете недостаточно средств")
    void depositPaidTest() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        UserEvent userEvent = UserEvent.builder()
                .userId(userId)
                .gender(false)
                .firstName("Иван")
                .lastName("Филипов")
                .status(UserEventStatus.CREATED)
                .build();
        billingService.createAccounts(userEvent);

        try (KafkaProducer<String, OrderEvent> producer = KafkaTestUtil.createKafkaProducer(kafka.getBootstrapServers())) {
            var orderEvent = OrderEvent.builder()
                    .userId(userId)
                    .managerId(managerId)
                    .currency(CurrencyEnumDto.RUB)
                    .orderId(orderId)
                    .status(WAITING_PAYMENT_DEPOSIT)
                    .items(List.of(
                            OrderItemEvent.builder()
                                    .price(BigDecimal.valueOf(10002))
                                    .depositSum(BigDecimal.valueOf(1000))
                                    .productId(UUID.randomUUID())
                                    .quantity(2)
                                    .build(),
                            OrderItemEvent.builder()
                                    .price(BigDecimal.valueOf(100))
                                    .depositSum(BigDecimal.valueOf(1002))
                                    .productId(UUID.randomUUID())
                                    .quantity(3)
                                    .build()
                    ))
                    .build();
            producer.send(new ProducerRecord<>(ORDER_EVENT_TOPIC, orderEvent));
        }

        Awaitility.await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    List<BillingOrderOutboxEntity> outbox = outboxRepository.findAll();
                    assertThat(outbox.size()).isEqualTo(1);
                    BillingOrderOutboxEntity billingOrderOutboxEntity = outbox.stream().findFirst().get();
                    assertThat(billingOrderOutboxEntity.getOrderId()).isEqualTo(orderId);
                    assertThat(billingOrderOutboxEntity.getStatus()).isEqualTo(BillingEventStatus.DEPOSIT_PAYMENT_REJECTED);
                    assertThat(billingOrderOutboxEntity.getErrorMessage()).isEqualTo("На счете недостаточно средств");
                });
    }

    @Test
    @DisplayName("На счете достаточно средств")
    void depositPaidTest2() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        UserEvent userEvent = UserEvent.builder()
                .userId(userId)
                .gender(false)
                .firstName("Иван")
                .lastName("Филипов")
                .status(UserEventStatus.CREATED)
                .build();
        UserEvent userEvent2 = UserEvent.builder()
                .userId(managerId)
                .gender(false)
                .firstName("Гена")
                .lastName("Иванов")
                .status(UserEventStatus.CREATED)
                .build();
        billingService.createAccounts(userEvent);//user
        billingService.createAccounts(userEvent2);//manager

        billingService.changeBalance(Account.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(10000000))
                .build());

        try (KafkaProducer<String, OrderEvent> producer = KafkaTestUtil.createKafkaProducer(kafka.getBootstrapServers())) {
            var orderEvent = OrderEvent.builder()
                    .userId(userId)
                    .managerId(managerId)
                    .currency(CurrencyEnumDto.RUB)
                    .orderId(orderId)
                    .status(WAITING_PAYMENT_DEPOSIT)
                    .items(List.of(
                            OrderItemEvent.builder()
                                    .price(BigDecimal.valueOf(10002))
                                    .depositSum(BigDecimal.valueOf(1000))
                                    .productId(UUID.randomUUID())
                                    .quantity(2)
                                    .build(),
                            OrderItemEvent.builder()
                                    .price(BigDecimal.valueOf(100))
                                    .depositSum(BigDecimal.valueOf(1002))
                                    .productId(UUID.randomUUID())
                                    .quantity(3)
                                    .build()
                    ))
                    .build();
            producer.send(new ProducerRecord<>(ORDER_EVENT_TOPIC, orderEvent));
        }

        Awaitility.await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    assertThat(outboxRepository.findAll().size()).isEqualTo(1);
                });
    }

}
