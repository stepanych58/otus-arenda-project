package ru.otus.msa.order.adapter.out.pg;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.msa.order.adapter.out.pg.entity.MsaUserEntity;
import ru.otus.msa.order.adapter.out.pg.entity.OrderEntity;
import ru.otus.msa.order.adapter.out.pg.entity.OrderItemEntity;
import ru.otus.msa.order.adapter.out.pg.entity.ProductEntity;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.http.OrderDto;
import ru.otus.msa.order.api.http.OrderItemDto;
import ru.otus.msa.order.api.kafka.OrderEvent;

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

    default OrderEntity toCreateEntity(OrderDto orderDto, List<ProductEntity> products, MsaUserEntity user, MsaUserEntity msaManagerEntity) {
        OrderEntity order = toCreateEntityInner(orderDto, products);
        order.setUser(user);
        order.setManager(msaManagerEntity);
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
        Map<UUID, ProductEntity> productPriceMap = products.stream()
                .collect(Collectors.toMap(ProductEntity::getProductId, v -> v));
        for (OrderItemDto orderItemDto : items) {
            result.add(mapOrderItem(orderItemDto, productPriceMap.get(orderItemDto.productId())));
        }

        return result;
    }

    @Mapping(target = "id", source = "orderItemDto.id")
    @Mapping(target = "price", source = "productEntity.price")
    @Mapping(target = "depositSum", source = "productEntity.depositSum")
    @Mapping(target = "productId", source = "orderItemDto.productId")
    @Mapping(target = "quantity", source = "orderItemDto.quantity")
    OrderItemEntity mapOrderItem(OrderItemDto orderItemDto, ProductEntity productEntity);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "userId", source = "orderEntity.user.id")
    @Mapping(target = "managerId", source = "orderEntity.manager.id")
    @Mapping(target = "orderName", source = "orderEntity.name")
    @Mapping(target = "pickUpAddress", source = "orderEntity.manager.pickupAddress")
    @Mapping(target = "pickUpTimes", expression = "java(orderEntity.getManager().getPickupStartTime() + \" - \" + orderEntity.getManager().getPickupEndTime())")
    OrderEvent toEvent(OrderEntity orderEntity);

    @Mapping(target = "currentStatus", source = "status")
    OrderDto toDto(OrderEntity orderEntity);

    @Mapping(target = "currentStatus", source = "orderEntity.status")
    @Mapping(target = "userId", source = "orderEntity.user.id")
    @Mapping(target = "managerId", source = "orderEntity.manager.id")
    OrderDto toDto(OrderEntity orderEntity, List<OrderStatus> statusHistory);
}
