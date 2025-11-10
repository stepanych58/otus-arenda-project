package ru.otus.msa.order.api.http;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(UUID id,
                           @NotNull UUID productId,
                           @NotEmpty Integer quantity,
                           BigDecimal price) {
}
