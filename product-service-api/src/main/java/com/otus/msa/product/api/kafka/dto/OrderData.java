package com.otus.msa.product.api.kafka.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderData(@NotNull UUID orderId) {
}
