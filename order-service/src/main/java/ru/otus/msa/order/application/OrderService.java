package ru.otus.msa.order.application;

import com.otus.msa.product.api.kafka.dto.ProductOrderEvent;
import jakarta.validation.Valid;
import ru.otus.msa.order.adapter.in.http.AdminReceiveDto;
import ru.otus.msa.order.adapter.in.http.ClientReceiveDto;
import ru.otus.msa.order.adapter.out.pg.entity.OrderEntity;
import ru.otus.msa.order.api.http.OrderDto;
import ru.otus.msa.user.api.kafka.dto.BillingEvent;

import java.util.UUID;

public interface OrderService {
    OrderDto createOrder(UUID requestId, OrderDto order);

    OrderEntity save(OrderEntity order);

    OrderEntity getOrderEntity(UUID orderId);

    OrderDto getOrderDto(UUID orderId);

    OrderDto clientReceive(UUID orderId, @Valid ClientReceiveDto clientReceiveDto);

    OrderDto adminReceive(UUID orderId, @Valid AdminReceiveDto adminReceiveDto);

    void updateByProductEvent(ProductOrderEvent productEvent);

    void updateByBillingEvent(BillingEvent billingEvent);
}
