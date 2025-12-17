package ru.otus.msa.notification.api.http.dto;

import java.util.UUID;

public record NotificationDto(UUID id, UUID userId, UUID orderId, String content) {
}
