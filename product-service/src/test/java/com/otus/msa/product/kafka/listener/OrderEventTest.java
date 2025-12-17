package com.otus.msa.product.kafka.listener;

import com.otus.msa.product.BaseContainerTest;
import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;
import com.otus.msa.product.adapter.out.pg.entity.ProductReservationEntity;
import com.otus.msa.product.adapter.out.pg.repository.ProductReservationEntityRepository;
import com.otus.msa.product.domain.ProductService;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.otus.msa.order.api.http.CurrencyEnumDto;
import ru.otus.msa.order.api.kafka.OrderEvent;
import ru.otus.msa.order.api.kafka.OrderItemEvent;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.otus.msa.order.api.common.OrderStatus.WAITING_RESERVE_PRODUCT;
import static ru.otus.msa.order.api.common.OrderStatus.WAITING_REVERT_RESERVE_PRODUCT;

@Log4j2
public class OrderEventTest extends BaseContainerTest {

    Producer<String, OrderEvent> ORDER_EVENT_PRODUCER = new KafkaProducer<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class
    ));

    KafkaConsumer<String, OrderEvent> orderEventKafkaConsumer = new KafkaConsumer<String, OrderEvent>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, "OrderEventTest"
    ));

    @Autowired
    private ProductReservationEntityRepository productReservationRepository;

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("Тестирование резервирования и отмены резервирования продуктов")
    void reservationProductsTest() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID firstProductId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        ProductEntity productEntity = productService.getOne(firstProductId);
        Integer initialProductQuantity = productEntity.getQuantity();
        int expectedQuantity = initialProductQuantity - 1;
        OrderEvent waiteReserveProductEvent = OrderEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .status(WAITING_RESERVE_PRODUCT)
                .currency(CurrencyEnumDto.RUB)
                .items(
                        List.of(
                                OrderItemEvent.builder()
                                        .id(UUID.randomUUID())
                                        .productId(firstProductId)
                                        .quantity(1)
                                        .build(),
                                OrderItemEvent.builder()
                                        .id(UUID.randomUUID())
                                        .productId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                                        .quantity(3)
                                        .build()
                        )
                )
                .build();
        ORDER_EVENT_PRODUCER.send(new ProducerRecord<>(
                ORDER_EVENT_TOPIC, waiteReserveProductEvent
        ));

        Awaitility.await()
                .atMost(Duration.of(15, ChronoUnit.SECONDS))
                .untilAsserted(() -> {
                    List<ProductReservationEntity> reservedProducts = productReservationRepository.findAll();
                    assertThat(reservedProducts.size()).isEqualTo(2);
                    ProductEntity product = productService.getOne(firstProductId);
                    assertThat(product.getQuantity()).isEqualTo(expectedQuantity);
                });

        OrderEvent waiteRevertReserveProductEvent = OrderEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .status(WAITING_REVERT_RESERVE_PRODUCT)
                .currency(CurrencyEnumDto.RUB)
                .items(
                        List.of(
                                OrderItemEvent.builder()
                                        .id(UUID.randomUUID())
                                        .productId(firstProductId)
                                        .quantity(1)
                                        .build(),
                                OrderItemEvent.builder()
                                        .id(UUID.randomUUID())
                                        .productId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                                        .quantity(3)
                                        .build()
                        )
                )
                .build();
        ORDER_EVENT_PRODUCER.send(new ProducerRecord<>(ORDER_EVENT_TOPIC, waiteRevertReserveProductEvent));
        Awaitility.await()
                .atMost(Duration.of(15, ChronoUnit.SECONDS))
                .untilAsserted(() -> {
                    List<ProductReservationEntity> reservedProducts = productReservationRepository.findAll();
                    assertThat(reservedProducts.size()).isEqualTo(0);
                    ProductEntity product = productService.getOne(firstProductId);
                    assertThat(product.getQuantity()).isEqualTo(initialProductQuantity);
                });
    }

    @Test
    @DisplayName("Тест на ошибку резервирования продуктов")
    @Disabled
    void productReservationReject() {
        orderEventKafkaConsumer.subscribe(List.of(ORDER_EVENT_TOPIC));
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OrderEvent waiteReserveProductEvent = OrderEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .status(WAITING_RESERVE_PRODUCT)
                .currency(CurrencyEnumDto.RUB)
                .items(
                        List.of(
                                OrderItemEvent.builder()
                                        .id(UUID.randomUUID())
                                        .productId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                                        .quantity(3000)
                                        .build()
                        )
                )
                .build();

        ORDER_EVENT_PRODUCER.send(new ProducerRecord<>(ORDER_EVENT_TOPIC, waiteReserveProductEvent));
        ORDER_EVENT_PRODUCER.close();

        ConsumerRecords<String, OrderEvent> polled =
                orderEventKafkaConsumer.poll(Duration.of(1, ChronoUnit.MINUTES));
        polled.forEach(cr -> {
            log.info("cr-> {}", cr);
        });
        orderEventKafkaConsumer.close();
    }
}
