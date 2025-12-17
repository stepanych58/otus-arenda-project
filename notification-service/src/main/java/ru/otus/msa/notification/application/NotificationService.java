package ru.otus.msa.notification.application;

import java.util.List;
import java.util.UUID;

import ru.otus.msa.notification.api.http.dto.NotificationDto;
import ru.otus.msa.order.api.kafka.OrderEvent;

public interface NotificationService {
    List<NotificationDto> getNotifications(UUID userId, UUID orderId);

    void createNotification(OrderEvent orderEvent);
}
