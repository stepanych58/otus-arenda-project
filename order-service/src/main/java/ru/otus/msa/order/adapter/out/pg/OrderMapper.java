package ru.otus.msa.order.adapter.out.pg;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.msa.order.adapter.out.pg.repository.entity.OrderEntity;
import ru.otus.msa.order.adapter.out.pg.repository.entity.OrderItemEntity;
import ru.otus.msa.order.adapter.out.pg.repository.entity.ProductEntity;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.http.OrderDto;
import ru.otus.msa.order.api.http.OrderItemDto;
import ru.otus.msa.order.api.kafka.OrderEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "items", expression = "java(mapOrderItem(orderDto, products))")
    OrderEntity toCreateEntityInner(OrderDto orderDto, List<ProductEntity> products);

    default OrderEntity toCreateEntity(OrderDto orderDto, List<ProductEntity> products) {
        OrderEntity order = toCreateEntityInner(orderDto, products);
        order.setItems(order.getItems()
                .stream()
                .peek(oi -> oi.setOrder(order))
                .toList()
        );
        return order;
    }

    default List<OrderItemEntity> mapOrderItem(OrderDto orderDto, List<ProductEntity> products) {
        final var items = orderDto.items();
        if (items == null) {
            return null;
        }

        List<OrderItemEntity> result = new ArrayList<>(items.size());
        Map<UUID, BigDecimal> productPriceMap = products.stream()
                .collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getPrice));
        for (OrderItemDto orderItemDto : items) {
            result.add(mapOrderItem(orderItemDto, productPriceMap.get(orderItemDto.productId())));
        }

        return result;
    }

    @Mapping(target = "price", source = "productPrice")
    OrderItemEntity mapOrderItem(OrderItemDto orderItemDto, BigDecimal productPrice);

    OrderEvent toEvent(OrderEntity orderEntity);
    @Mapping(target = "currentStatus", source = "status")
    OrderDto toDto(OrderEntity orderEntity);
    @Mapping(target = "currentStatus", source = "orderEntity.status")
    OrderDto toDto(OrderEntity orderEntity, List<OrderStatus> statusHistory);
}
