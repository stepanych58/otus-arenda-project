package ru.otus.msa.user.api.kafka.dto;

import java.util.UUID;

import lombok.Builder;
import ru.otus.msa.user.api.BillingEventStatus;

@Builder
public record BillingEvent(
        UUID eventId,
        UUID paymentId,
        UUID orderId,
        BillingEventStatus status,
        String errorMessage) {
}
