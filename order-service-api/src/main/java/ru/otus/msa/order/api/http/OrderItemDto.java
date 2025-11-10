package ru.otus.msa.order.api.http;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderItemDto(UUID id,
                           @NotNull UUID productId,
                           @NotEmpty Integer quantity,
                           BigDecimal price) {
}
