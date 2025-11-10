package ru.otus.msa.order.application.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID orderId) {
        super(String.format("Заказ не найден %s", orderId));
    }
}
