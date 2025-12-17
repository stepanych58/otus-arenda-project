package ru.otus.msa.order.api.http;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.otus.msa.order.api.common.OrderStatus;

import java.util.List;
import java.util.UUID;

@Builder
public record OrderDto(UUID id,
                       @NotNull UUID userId,
                       UUID managerId,
                       OrderStatus currentStatus,
                       List<OrderStatus> statusHistory,
                       String rentStartDate,
                       String rentCompleteDate,
                       String rejectReason,
                       @NotNull CurrencyEnumDto currency,
                       @NotEmpty List<OrderItemDto> items) {
}
