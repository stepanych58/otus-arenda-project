package ru.otus.msa.order.application;

import org.springframework.kafka.support.SendResult;
import ru.otus.msa.order.api.kafka.OrderEvent;

import java.util.concurrent.CompletableFuture;

public interface OrderSender {
    CompletableFuture<SendResult<String, OrderEvent>> send(OrderEvent event);
}
