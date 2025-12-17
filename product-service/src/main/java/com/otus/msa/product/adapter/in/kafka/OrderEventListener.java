package com.otus.msa.product.adapter.in.kafka;

import com.otus.msa.product.api.kafka.dto.ProductEventState;
import com.otus.msa.product.application.exception.ReservationException;
import com.otus.msa.product.domain.ProductEventFactory;
import com.otus.msa.product.domain.ProductEventSender;
import com.otus.msa.product.domain.ProductReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.msa.order.api.kafka.OrderEvent;

import static ru.otus.msa.order.api.common.OrderStatus.WAITING_RESERVE_PRODUCT;
import static ru.otus.msa.order.api.common.OrderStatus.WAITING_REVERT_RESERVE_PRODUCT;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderEventListener {

    private final ProductReservationService productService;

    private final ProductEventSender productEventSender;

    private final ProductEventFactory productEventFactory;

    @KafkaListener(topics = {"order-event"},
            containerFactory = "orderEventContainerFactory")
    public void onMessage(OrderEvent orderEvent) {
        try {
            ProductEventState state;
            if (WAITING_RESERVE_PRODUCT == orderEvent.status()) {
                productService.createReservation(orderEvent);
                state = ProductEventState.RESERVED;
            } else if (WAITING_REVERT_RESERVE_PRODUCT == orderEvent.status()) {
                productService.cancelReservation(orderEvent);
                state = ProductEventState.RESERVATION_CANCEL;
            } else {
                return;
            }
            final var productEvents = productEventFactory.buildProductEvents(orderEvent.getProductIds(),
                    orderEvent.orderId(),
                    state);
            productEventSender.send(productEvents);
        } catch (ReservationException e) {
            log.error(e.getMessage());
            final var productEvents = productEventFactory.buildProductEvents(orderEvent.getProductIds(),
                    orderEvent.orderId(),
                    ProductEventState.RESERVATION_FAILED,
                    e.getMessage());
            productEventSender.send(productEvents);
        }
    }

}
