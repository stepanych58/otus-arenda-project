package com.otus.msa.product.domain;

import com.otus.msa.product.api.kafka.dto.ProductEvent;
import com.otus.msa.product.api.kafka.dto.ProductOrderEvent;

public interface ProductEventSender {
    void send(ProductOrderEvent event);

    void send(ProductEvent event);
}
