package ru.otus.msa.order.application;

import ru.otus.msa.order.api.http.OrderDto;
import ru.otus.msa.order.api.kafka.OrderEvent;

import java.util.UUID;


public interface OrderService {
    OrderDto createOrder(OrderDto order);

    void update(OrderEvent billingEvent);

    OrderDto getOrder(UUID orderId);
}
