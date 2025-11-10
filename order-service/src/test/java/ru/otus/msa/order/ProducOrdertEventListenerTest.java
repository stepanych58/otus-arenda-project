package ru.otus.msa.order;

import com.otus.msa.product.api.kafka.dto.OrderData;
import com.otus.msa.product.api.kafka.dto.ProductData;
import com.otus.msa.product.api.kafka.dto.ProductEventState;
import com.otus.msa.product.api.kafka.dto.ProductOrderEvent;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.otus.msa.order.adapter.out.pg.entity.OrderEntity;
import ru.otus.msa.order.adapter.out.pg.repository.OrderEntityRepository;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.msa.order.KafkaTestUtil.rec;
import static ru.otus.msa.order.api.common.OrderStatus.WAITING_PAYMENT_DEPOSIT;

@Log4j2
public class ProducOrdertEventListenerTest extends BaseContainerTest {

    @Autowired
    private OrderEntityRepository orderEntityRepository;

    @Test
    @DisplayName("Тестирование резервирования product")
    @Sql("classpath:/sql/scripts/reserve_product_case/init.sql")
    public void test() {
        log.info("start test");
        ProductOrderEvent productEvent = ProductOrderEvent.builder()
                .eventId(UUID.randomUUID())
                .product(List.of(ProductData.builder()
                        .id(UUID.fromString("2a6629c1-2180-4a7e-a0ce-305f46cc4b71"))
                        .name("Палатка 2х местная")
                        .quantity(11)
                        .build()))
                .order(OrderData.builder()
                        .orderId(UUID.fromString("4e232b78-8183-484a-9d50-2b693f8d99b2"))
                        .build())
                .eventState(ProductEventState.RESERVED)
                .build();

        try (Producer<String, ProductOrderEvent> producer = KafkaTestUtil.createKafkaProducer(kafka.getBootstrapServers())) {
            producer.send(rec(PRODUCT_ORDER_EVENT_TOPIC, null, productEvent));
        }

        Awaitility.await()
                .atMost(Duration.of(1, ChronoUnit.MINUTES))
                .untilAsserted(() -> {
                    assertThat(orderEntityRepository.findAll().size()).isEqualTo(1);
                    OrderEntity first = orderEntityRepository.findAll().getFirst();
                    assertThat(first.getStatus()).isEqualTo(WAITING_PAYMENT_DEPOSIT);
                });
    }
}
