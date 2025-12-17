package ru.otus.msa.order.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.msa.order.adapter.out.pg.OrderMapper;
import ru.otus.msa.order.adapter.out.pg.entity.OrderEntity;
import ru.otus.msa.order.adapter.out.pg.entity.OrderHistory;
import ru.otus.msa.order.adapter.out.pg.repository.OrderHistoryRepository;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.kafka.OrderEvent;

import java.util.concurrent.ExecutionException;

import static ru.otus.msa.order.api.common.OrderStatus.*;

@Service
@RequiredArgsConstructor
public class OrderOutboxProcessor {

    private final OrderService orderService;
    private final OrderHistoryRepository historyRepository;
    private final OrderSender sender;
    private final OrderMapper mapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public void process(OrderHistory orderHistory) throws JsonProcessingException,
            ExecutionException, InterruptedException {
        OrderEntity orderEntity = orderService.getOrderEntity(orderHistory.getOrderId());
        if (orderHistory.getStatus().equals(CREATED)) {
            send(orderHistory, orderEntity, WAITING_RESERVE_PRODUCT);
        } else if (orderHistory.getStatus().equals(PAYMENT_DEPOSIT_REJECT) ||
                orderHistory.getStatus().equals(CLIENT_RECEIVE_REJECT)) {
            send(orderHistory, orderEntity, WAITING_REVERT_RESERVE_PRODUCT);
        } else if (orderHistory.getStatus().equals(PAYMENT_PRODUCT_REJECT)) {
            send(orderHistory, orderEntity, WAITING_CLIENT_RECEIVE);
        } else if (orderHistory.getStatus().equals(RESERVE_PRODUCT_REJECT)) {
            send(orderHistory, orderEntity, REJECTED);
        } else {
            OrderEvent orderEvent = mapper.toEvent(orderEntity);
            orderHistory.setSentObject(objectMapper.writeValueAsString(orderEvent));
            orderHistory.setSent(true);
            sender.send(orderEvent).get();
        }
        historyRepository.save(orderHistory);

    }

    private void send(OrderHistory orderHistory,
                      OrderEntity orderEntity, OrderStatus nextStatus) throws JsonProcessingException {
        OrderEvent orderEvent = mapper.toEvent(orderEntity);
        orderHistory.setSentObject(objectMapper.writeValueAsString(orderEvent));
        orderHistory.setSent(true);
        orderEntity.setStatus(nextStatus);
        orderService.save(orderEntity);
        sender.send(orderEvent);
    }
}
