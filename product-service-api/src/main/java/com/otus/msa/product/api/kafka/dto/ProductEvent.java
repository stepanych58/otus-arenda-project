package com.otus.msa.product.api.kafka.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

@Builder
public record ProductEvent(
        UUID eventId,
        UUID productId,
        UUID userId,
        String name,
        String description,
        BigDecimal price,
        BigDecimal depositSum,
        Integer quantity,
        String imgUrl,
        String currency,
        Boolean rmv
) {
}
