package ru.otus.msa.order;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.msa.order.KafkaTestUtil.rec;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import com.otus.msa.product.api.kafka.dto.ProductEvent;
import lombok.extern.log4j.Log4j2;
import ru.otus.msa.order.adapter.out.pg.repository.ProductEntityRepository;

@Log4j2
public class ProductEventListenerTest extends BaseContainerTest {

    @Autowired
    private ProductEntityRepository productEntityRepository;

    @Test
    @DisplayName("Тестирование сохранения product")
    public void test() {
        log.info("start test");
        ProductEvent productEvent = ProductEvent.builder()
                .userId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .quantity(100)
                .depositSum(BigDecimal.valueOf(2000))
                .price(BigDecimal.valueOf(3000))
                .name("Test product")
                .description("Test product")
                .imgUrl("/img.png")
                .rmv(false)
                .currency("RUB")
                .build();

        try (Producer<String, ProductEvent> producer = KafkaTestUtil.createKafkaProducer(kafka.getBootstrapServers())) {
            producer.send(rec(PRODUCT_EVENT_TOPIC, null, productEvent));
        }

        Awaitility.await()
                .atMost(Duration.of(1, ChronoUnit.MINUTES))
                .untilAsserted(() -> {
                    assertThat(productEntityRepository.findAll().size()).isEqualTo(1);
                });
    }
}
