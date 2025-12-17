package com.otus.msa.product.domain;

import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;
import ru.otus.msa.order.api.kafka.OrderEvent;

public interface ProductReservationService {
    void createReservation(OrderEvent orderEvent);

    void cancelReservation(OrderEvent orderEvent);

    void createByProduct(ProductEntity productEntity);
}
