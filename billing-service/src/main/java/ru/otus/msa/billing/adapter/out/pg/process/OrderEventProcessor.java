package ru.otus.msa.billing.adapter.out.pg.process;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.msa.billing.application.BillingService;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.kafka.OrderEvent;
import ru.otus.msa.user.api.BillingEventStatus;

import java.util.List;

@RequiredArgsConstructor
public abstract class OrderEventProcessor {

    protected final BillingService billingService;

    @Transactional
    abstract void process(OrderEvent orderEvent);

    abstract List<OrderStatus> getStatus();

    abstract BillingEventStatus getFailedStatus();

    @Autowired
    void init(OrderEventProcessorsRegistry registry) {
        getStatus().forEach(status ->
                registry.register(status, this));
    }

    protected void processException(OrderEvent orderEvent, Exception e) {
        billingService.createBillingEventOutbox(orderEvent.orderId(),
                null,
                getFailedStatus(),
                e.getMessage());
    }
}