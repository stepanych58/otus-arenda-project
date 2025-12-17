package ru.otus.msa.order.domain;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

@Builder
public class OrderItemEntityDomain {
    private UUID id;

    private UUID productId;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal deposit;
}
