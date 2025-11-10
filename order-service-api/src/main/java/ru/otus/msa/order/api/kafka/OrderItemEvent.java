package ru.otus.msa.order.api.kafka;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemEvent(UUID id, UUID productId, Integer quantity, BigDecimal price) {

    public BigDecimal getCoast() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
