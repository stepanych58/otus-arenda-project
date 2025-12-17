package com.otus.msa.product.api.kafka.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record ProductOrderEvent(
        UUID eventId,
        List<ProductData> product,
        OrderData order,
        ProductEventState eventState, //CREATE, UPDATE, RESERVED, RESERVATION_FAILED
        //todo добавить обработку отказов
        String errorMessage,
        String cancelReason
) {
}
