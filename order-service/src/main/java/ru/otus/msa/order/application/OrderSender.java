package ru.otus.msa.order.application;

import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

import ru.otus.msa.order.api.kafka.OrderEvent;

public interface OrderSender {
    CompletableFuture<SendResult<String, OrderEvent>> send(OrderEvent event);
}
