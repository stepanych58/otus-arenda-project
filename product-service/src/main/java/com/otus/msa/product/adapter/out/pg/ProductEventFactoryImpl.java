package com.otus.msa.product.adapter.out.pg;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.otus.msa.product.api.kafka.dto.OrderData;
import com.otus.msa.product.api.kafka.dto.ProductData;
import com.otus.msa.product.api.kafka.dto.ProductEventState;
import com.otus.msa.product.api.kafka.dto.ProductOrderEvent;
import com.otus.msa.product.domain.ProductEventFactory;
import com.otus.msa.product.domain.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductEventFactoryImpl implements ProductEventFactory {

    private final ProductService productService;

    @Override
    public ProductOrderEvent buildProductEvents(List<UUID> productIds,
                                                UUID orderId,
                                                ProductEventState state) {
        return buildProductEvents(productIds, orderId, state, null);
    }

    @Override
    public ProductOrderEvent buildProductEvents(List<UUID> productIds, UUID orderId, ProductEventState state, String errorMessage) {
        List<ProductData> productDataList = productService.getMany(productIds)
                .stream()
                .map(productEntity ->
                             new ProductData(productEntity.getId(), productEntity.getName(), productEntity.getQuantity()))
                .toList();
        OrderData orderData = new OrderData(orderId);
        return new ProductOrderEvent(UUID.randomUUID(), productDataList, orderData, state, errorMessage, null);
    }
}
