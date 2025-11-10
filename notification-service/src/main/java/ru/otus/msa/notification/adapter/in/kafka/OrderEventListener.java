package ru.otus.msa.notification.adapter.in.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.otus.msa.notification.application.NotificationService;
import ru.otus.msa.order.api.kafka.OrderEvent;

@Service
@RequiredArgsConstructor
public class OrderEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = { "order-event" },
            containerFactory = "orderEventContainerFactory")
    public void onMessage(OrderEvent orderEvent) {
        notificationService.createNotification(orderEvent);
    }
}
