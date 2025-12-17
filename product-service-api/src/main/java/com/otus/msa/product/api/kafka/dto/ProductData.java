package com.otus.msa.product.api.kafka.dto;

import java.util.UUID;

import lombok.Builder;

@Builder
public record ProductData(
        UUID id, String name, Integer quantity
) {
}
