package ru.otus.msa.order.adapter.in.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.msa.order.api.kafka.OrderEvent;
import ru.otus.msa.order.application.OrderService;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderService orderService;

    @KafkaListener(topics = {"order-event"}, containerFactory = "orderEventContainerFactory")
    public void onMessage(OrderEvent event) {
        log.info("order-event message: {}", event);
        orderService.update(event);
    }

}
