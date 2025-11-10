package ru.otus.msa.order.api.http;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.otus.msa.order.api.common.OrderStatus;

import java.util.List;
import java.util.UUID;

public record OrderDto(UUID id,
                       @NotNull UUID userId,
                       OrderStatus currentStatus,
                       List<OrderStatus> statusHistory,
                       String rejectReason,
                       @NotNull CurrencyEnumDto currency,
                       @NotEmpty List<OrderItemDto> items) {
}
