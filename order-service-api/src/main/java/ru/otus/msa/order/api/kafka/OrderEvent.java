package ru.otus.msa.order.api.kafka;

import lombok.Builder;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.http.CurrencyEnumDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Builder(toBuilder = true)
public record OrderEvent(UUID id,
                         UUID userId,
                         OrderStatus status,
                         String rejectReason,
                         String revertReason,
                         CurrencyEnumDto currency,
                         List<OrderItemEvent> items) {
    public BigDecimal getCoast() {
        return Optional.ofNullable(items)
                .map(i -> i.stream()
                        .map(OrderItemEvent::getCoast)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
    }

    public OrderEvent paymentRejected(String rejectReason) {
        return toBuilder()
                .rejectReason(rejectReason)
                .status(OrderStatus.PAYMENT_REJECT)
                .build();
    }

    public OrderEvent paymentCompleted() {
        return toBuilder()
                .status(OrderStatus.PAYED)
                .build();
    }

    public OrderEvent paymentReverted() {
        return toBuilder()
                .status(OrderStatus.PAYMENT_REVERTED)
                .build();
    }

    public OrderEvent productsReserved() {
        return toBuilder()
                .status(OrderStatus.PRODUCT_RESERVED)
                .build();
    }

    public OrderEvent rejectProductReservation(String rejectReason) {
        return toBuilder()
                .rejectReason(rejectReason)
                .status(OrderStatus.RESERVE_PRODUCT_REJECT)
                .build();
    }

    public OrderEvent productsReverted() {
        return toBuilder()
                .status(OrderStatus.PRODUCT_REVERTED)
                .build();
    }

    public OrderEvent rejectDeliveryReservation(String rejectReason) {
        return toBuilder()
                .rejectReason(rejectReason)
                .status(OrderStatus.RESERVE_DELIVERY_REJECT)
                .build();
    }

    public OrderEvent deliveryReserved() {
        return toBuilder()
                .status(OrderStatus.DELIVERY_RESERVED)
                .build();
    }
}