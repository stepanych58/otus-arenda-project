package ru.otus.msa.billing.adapter.in.http.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO Заказа
 *
 * @param orderId    ID заказа
 * @param orderItems список товаров в заказе
 * @param userId     ID пользователя который сделал заказ
 * @param createdAt  когда был сделан заказ
 */
public record OrderDto(UUID orderId, List<OrderItemDto> orderItems,
                       UUID userId, LocalDateTime createdAt) {
    /**
     * Общая стоимость всего заказа
     *
     * @return -
     */
    public BigDecimal getCoast() {
        return orderItems.stream()
                .map(OrderItemDto::getCoast)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
