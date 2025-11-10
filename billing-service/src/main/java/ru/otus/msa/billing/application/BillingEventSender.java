package ru.otus.msa.billing.application;

import ru.otus.msa.user.api.kafka.dto.BillingEvent;

import java.util.concurrent.ExecutionException;

public interface BillingEventSender {
    void send(BillingEvent orderEvent) throws ExecutionException, InterruptedException;
}
