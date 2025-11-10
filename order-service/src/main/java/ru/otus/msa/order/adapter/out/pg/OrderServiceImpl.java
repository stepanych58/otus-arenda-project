package ru.otus.msa.order.adapter.out.pg;

import com.otus.msa.product.api.kafka.dto.ProductOrderEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import ru.otus.msa.order.adapter.in.http.AdminReceiveDto;
import ru.otus.msa.order.adapter.in.http.ClientReceiveDto;
import ru.otus.msa.order.adapter.out.pg.entity.*;
import ru.otus.msa.order.adapter.out.pg.repository.*;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.http.OrderDto;
import ru.otus.msa.order.api.http.OrderItemDto;
import ru.otus.msa.order.application.OrderService;
import ru.otus.msa.order.application.exception.IncorrectOrderException;
import ru.otus.msa.order.application.exception.OrderNotFoundException;
import ru.otus.msa.user.api.kafka.dto.BillingEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static ru.otus.msa.order.api.common.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderEntityRepository orders;

    private final OrderItemEntityRepository orderItems;

    private final ProductEntityRepository products;

    private final MsaUserRepository users;

    private final OrderMapper orderMapper;

    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderRequestRepository orderRequestRepository;

    @Override
    @Transactional
    public OrderDto createOrder(UUID requestId, OrderDto order) {
        Optional<OrderRequest> byRequestId = orderRequestRepository.findByRequestId(requestId);
        if (byRequestId.isPresent()) {
            return getOrderDto(byRequestId.get().getOrderId());
        } else {
            MsaUserEntity msaUserEntity = users.findById(order.userId())
                    .orElseThrow(() -> new IncorrectOrderException(String.format("Не  найден пользователь с id %s", order.userId())));

            List<ProductEntity> productList = validatedProducts(order);
            UUID managerId = productList.getFirst().getUserId();
            MsaUserEntity msaManagerEntity = users.findById(managerId)
                    .orElseThrow(() -> new IncorrectOrderException(String.format("Не  найден пользователь с id %s", managerId)));
            OrderEntity toSave = orderMapper.toCreateEntity(order, productList, msaUserEntity, msaManagerEntity);
            toSave.setName(generateOrderName(msaUserEntity));
            OrderEntity savedOrder = save(toSave);
            orderItems.saveAll(savedOrder.getItems());
            orderRequestRepository.save(new OrderRequest(savedOrder.getId(), requestId));
            OrderDto result = orderMapper.toDto(savedOrder);
            log.info("Получен заказ: {}", result);
            return result;
        }
    }

    private String generateOrderName(MsaUserEntity user) {
        Integer count = orders.countByUser_Id(user.getId());
        return "Заказ №" + (count + 1);
    }

    public OrderEntity save(OrderEntity entity) {
        OrderEntity saved = orders.save(entity);
        logOrderHistory(saved);
        return saved;
    }

    private List<ProductEntity> validatedProducts(OrderDto order) {
        if (CollectionUtils.isEmpty(order.items())) {
            throw new IncorrectOrderException("В заказе нету товаров");
        }
        List<UUID> productIds = order.items().stream().map(OrderItemDto::productId).toList();
        List<ProductEntity> products = this.products.findAllByProductIdIn(productIds);
        if (products.size() != productIds.size()) {
            throw new IncorrectOrderException("В заказе присутствуют несуществующие продукты");
        }
        return products;
    }

    private void logOrderHistory(OrderEntity orderEntity) {
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setStatus(orderEntity.getStatus());
        orderHistory.setOrderId(orderEntity.getId());
        orderHistory.setSent(false);
        OrderHistory saved = orderHistoryRepository.save(orderHistory);
        log.info("history log: {} {} {}", saved.getId(), saved.getOrderId(), saved.getStatus());
    }

    @Override
    public OrderEntity getOrderEntity(UUID orderId) {
        return orders.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public OrderDto getOrderDto(UUID orderId) {
        OrderEntity orderEntity = getOrderEntity(orderId);
        List<OrderStatus> statusHistory = orderHistoryRepository.findAllByOrderIdOrderByCreatedAt(orderId)
                .stream()
                .map(OrderHistory::getStatus)
                .toList();
        return orderMapper.toDto(orderEntity, statusHistory);
    }


    @Override
    public OrderDto clientReceive(UUID orderId, @Valid ClientReceiveDto clientReceiveDto) {
        OrderEntity orderEntity = getOrderEntity(orderId);

        var newStatus = valueOf(clientReceiveDto.orderStatus().name());

        orderEntity.setStatus(newStatus);
        if (Objects.nonNull(clientReceiveDto.rejectReason())) {
            orderEntity.setRejectReason(clientReceiveDto.rejectReason());
        }
        return orderMapper.toDto(save(orderEntity));
    }

    @Override
    public OrderDto adminReceive(UUID orderId, @Valid AdminReceiveDto adminReceiveDto) {
        OrderEntity orderEntity = getOrderEntity(orderId);
        var newStatus = valueOf(adminReceiveDto.orderStatus().name());
        orderEntity.setStatus(newStatus);
        if (Objects.nonNull(adminReceiveDto.rejectReason())) {
            orderEntity.setRejectReason(adminReceiveDto.rejectReason());
        }
        return orderMapper.toDto(save(orderEntity));
    }

    @Override
    @Transactional
    public void updateByProductEvent(ProductOrderEvent productEvent) {
        UUID orderId = productEvent.order().orderId();
        Pair<String, OrderStatus> nextState = switch (productEvent.eventState()) {
            case RESERVED -> Pair.of(productEvent.cancelReason(), WAITING_PAYMENT_DEPOSIT);
            case RESERVATION_CANCEL -> Pair.of(productEvent.cancelReason(),
                    isOrderSuccess(orderId) ?
                            COMPLETED :
                            REJECTED);
            case RESERVATION_FAILED -> Pair.of(productEvent.errorMessage(), RESERVE_PRODUCT_REJECT);
        };
        OrderEntity order = getOrderEntity(orderId);
        order.setRejectReason(nextState.getLeft());
        order.setStatus(nextState.getRight());
        save(order);
    }

    @Override
    @Transactional
    public void updateByBillingEvent(BillingEvent billingEvent) {
        OrderEntity orderEntity = getOrderEntity(billingEvent.orderId());
        Pair<String, OrderStatus> nextState = switch (billingEvent.status()) {
            case DEPOSIT_PAID -> Pair.of(billingEvent.errorMessage(), WAITING_CLIENT_RECEIVE);
            case DEPOSIT_PAYMENT_REJECTED -> Pair.of(billingEvent.errorMessage(), PAYMENT_DEPOSIT_REJECT);
            case PRODUCT_PAID -> Pair.of(billingEvent.errorMessage(), WAITING_ADMIN_RECEIVE);
            case PRODUCT_PAYMENT_REJECTED -> Pair.of(billingEvent.errorMessage(), PAYMENT_PRODUCT_REJECT);
            case DEPOSIT_RETURNED -> Pair.of(billingEvent.errorMessage(), WAITING_REVERT_RESERVE_PRODUCT);
            case DEPOSIT_RETURN_REJECT -> Pair.of(billingEvent.errorMessage(), RETURN_DEPOSIT_ERROR);
            case DEPOSIT_MANAGER_ACCRUED -> Pair.of(billingEvent.errorMessage(),
                    isAdminReceiveError(billingEvent.orderId()) ? REJECTED : WAITING_REVERT_RESERVE_PRODUCT);
            case DEPOSIT_MANAGER_ACCRUED_ERROR -> Pair.of(billingEvent.errorMessage(), REJECTED);
        };

        orderEntity.setRejectReason(nextState.getLeft());
        orderEntity.setStatus(nextState.getRight());
        save(orderEntity);
    }

    private boolean isOrderSuccess(UUID orderId) {
        return isHistoryStateExist(orderId, WAITING_RETURN_DEPOSIT);
    }

    private boolean isAdminReceiveError(UUID orderId) {
        return isHistoryStateExist(orderId, ADMIN_RECEIVE_REJECT);
    }

    private boolean isHistoryStateExist(UUID orderId, OrderStatus adminReceiveReject) {
        return orderHistoryRepository.findAllByOrderIdOrderByCreatedAt(orderId)
                .stream()
                .map(OrderHistory::getStatus)
                .anyMatch(s -> s.equals(adminReceiveReject));
    }
}
