package com.otus.msa.product.domain;

import java.util.List;
import java.util.UUID;

import com.otus.msa.product.api.kafka.dto.ProductEventState;
import com.otus.msa.product.api.kafka.dto.ProductOrderEvent;

public interface ProductEventFactory {
    ProductOrderEvent buildProductEvents(List<UUID> productIds,
                                         UUID orderId,
                                         ProductEventState state);

    ProductOrderEvent buildProductEvents(List<UUID> productIds,
                                         UUID orderId,
                                         ProductEventState e,
                                         String errorMessage);
}
