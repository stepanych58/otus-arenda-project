package ru.otus.msa.notification.api.kafka.dto;

import java.util.UUID;

public record OrderNotificationEvent(UUID userId,
                                     UUID orderId,
                                     String orderStatus) {
}
