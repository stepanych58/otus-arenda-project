package ru.otus.msa.order.api.kafka;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.http.CurrencyEnumDto;

@Builder(toBuilder = true)
public record OrderEvent(@NotNull
                         UUID orderId,
                         String orderName,
                         String rentStartDate,
                         String rentCompleteDate,
                         String pickUpAddress,
                         String pickUpTimes,
                         @NotNull
                         UUID userId,
                         @NotNull
                         UUID managerId,
                         @NotNull
                         OrderStatus status,
                         String rejectReason,
                         String revertReason,
                         @NotNull
                         CurrencyEnumDto currency,
                         List<OrderItemEvent> items) {
    @Transient
    public BigDecimal getCoast() {
        return Optional.ofNullable(items)
                .map(i -> i.stream()
                        .map(OrderItemEvent::getCoast)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
    }

    @Transient
    public BigDecimal getDeposit() {
        return Optional.ofNullable(items)
                .map(i -> i.stream()
                        .map(OrderItemEvent::getDeposit)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
    }

    @Transient
    public List<UUID> getProductIds() {
        return Optional.ofNullable(items)
                .map(Collection::stream)
                .map(s -> s.map(OrderItemEvent::productId).toList())
                .orElse(List.of());
    }
}