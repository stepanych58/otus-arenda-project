package ru.otus.msa.order.adapter.out.pg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.otus.msa.order.adapter.out.pg.repository.*;
import ru.otus.msa.order.adapter.out.pg.repository.entity.OrderEntity;
import ru.otus.msa.order.adapter.out.pg.repository.entity.OrderHistory;
import ru.otus.msa.order.adapter.out.pg.repository.entity.ProductEntity;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.http.OrderDto;
import ru.otus.msa.order.api.http.OrderItemDto;
import ru.otus.msa.order.api.kafka.OrderEvent;
import ru.otus.msa.order.application.OrderSender;
import ru.otus.msa.order.application.OrderService;
import ru.otus.msa.order.application.exception.IncorrectOrderException;
import ru.otus.msa.order.application.exception.OrderNotFoundException;

import java.util.List;
import java.util.UUID;

import static ru.otus.msa.order.api.common.OrderStatus.COMPLETED;
import static ru.otus.msa.order.api.common.OrderStatus.CREATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderEntityRepository orders;
    private final OrderItemEntityRepository orderItems;
    private final ProductEntityRepository products;
    private final MsaUserRepository users;
    private final OrderMapper orderMapper;
    private final OrderSender orderSender;
    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto order) {
        users.findById(order.userId())
                .orElseThrow(IncorrectOrderException::new);
        List<ProductEntity> productList = validatedProducts(order);
        OrderEntity toSave = orderMapper.toCreateEntity(order, productList);
        OrderEntity savedOrder = save(toSave);
        orderItems.saveAll(savedOrder.getItems());
        orderSender.send(orderMapper.toEvent(savedOrder)).join();
        return orderMapper.toDto(savedOrder);
    }

    private OrderEntity save(OrderEntity entity) {
        OrderEntity saved = orders.save(entity);
        logOrderHistory(saved);
        return saved;
    }

    private List<ProductEntity> validatedProducts(OrderDto order) {
        if (CollectionUtils.isEmpty(order.items())) {
            throw new IncorrectOrderException();
        }
        List<UUID> productIds = order.items().stream().map(OrderItemDto::productId).toList();
        List<ProductEntity> products = this.products.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new IncorrectOrderException();
        }
        return products;
    }

    @Override
    @Transactional
    public void update(OrderEvent orderEvent) {
        List<OrderStatus> toSkip = List.of(CREATED);
        if (toSkip.contains(orderEvent.status())) {
            return;
        }
        OrderEntity orderEntity = orders.findById(orderEvent.id())
                .map(oe -> {
                    OrderStatus orderStatus = orderEvent.status();
                    return switch (orderStatus) {
                        case PAYED, PRODUCT_RESERVED, DELIVERY_RESERVED -> {
                            oe.setStatus(orderStatus);
                            OrderEntity saved = save(oe);
                            saved.setStatus(orderStatus.getNext());
                            yield saved;
                        }
                        case PAYMENT_REJECT, RESERVE_DELIVERY_REJECT,
                             RESERVE_PRODUCT_REJECT, PAYMENT_REVERTED,
                             PRODUCT_REVERTED, COMPLETED -> {
                            oe.setStatus(orderStatus);
                            oe.setRejectReason(orderEvent.rejectReason());
                            yield oe;
                        }
                        default -> oe;
                    };
                })
                .orElseThrow(() -> new OrderNotFoundException(orderEvent.id()));
        OrderEntity savedOrder = save(orderEntity);
    }

    private void logOrderHistory(OrderEntity orderEntity) {
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setStatus(orderEntity.getStatus());
        orderHistory.setOrderId(orderEntity.getId());
        OrderHistory saved = orderHistoryRepository.save(orderHistory);
        log.info("saved: {} {} {}", saved.getId(), saved.getOrderId(), saved.getStatus());
    }

    @Override
    public OrderDto getOrder(UUID orderId) {
        return orders.findById(orderId)
                .map(orderEntity -> {
                    List<OrderStatus> statusHistory = orderHistoryRepository.findAllByOrderIdOrderByCreatedAt(orderId)
                            .stream()
                            .map(OrderHistory::getStatus)
                            .toList();
                    return orderMapper.toDto(orderEntity, statusHistory);
                })
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
