package ru.otus.msa.order.adapter.out.kafka;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.msa.order.api.kafka.OrderEvent;
import ru.otus.msa.order.application.OrderSender;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSenderImpl implements OrderSender {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Override
    public CompletableFuture<SendResult<String, OrderEvent>> send(OrderEvent event) {
        String defaultTopic = kafkaTemplate.getDefaultTopic();
        return kafkaTemplate.send(defaultTopic, event.orderId().toString(), event)
                .whenComplete((res, err) -> {
                                  if (Objects.nonNull(err)) {
                                      log.error("Не удалось отправить order event", err);
                                      throw new RuntimeException(err);
                                  } else {
                                      log.info("Отправлено событие заказа {} {}", defaultTopic, event);
                                  }
                              }
                );
    }
}