package ru.otus.msa.order.adapter.in.http;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record AdminReceiveDto(@NotNull AdminOrderStatus orderStatus,
                              @Nullable String rejectReason) {
}
