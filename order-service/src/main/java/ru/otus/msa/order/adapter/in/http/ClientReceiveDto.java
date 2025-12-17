package ru.otus.msa.order.adapter.in.http;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record ClientReceiveDto(
        @NotNull ClientReceiveOrderStatus orderStatus,
        @Nullable String rejectReason) {
}
