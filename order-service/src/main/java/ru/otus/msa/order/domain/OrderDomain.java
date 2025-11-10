package ru.otus.msa.order.domain;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import ru.otus.msa.order.adapter.out.pg.entity.CurrencyEnum;
import ru.otus.msa.order.api.common.OrderStatus;

@Builder
public class OrderDomain {
    private UUID id;

    private UUID userId;

    private OrderStatus status;

    private String rejectReason;

    private List<OrderItemEntityDomain> items;

    private CurrencyEnum currency;
}
