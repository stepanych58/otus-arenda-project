package ru.otus.msa.order.application;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Service;

@Service
public class BusinessMetricService {
    public static final String ORDER_CREATED_COUNTER_LABEL = "MSA.BUSINESS.ORDER.CREATED.COUNTER";
    public static final String ORDER_SUCCESS_RENT_COUNTER_LABEL = "MSA.BUSINESS.ORDER.SUCCESS.RENT.COUNTER";
    public static final String ORDER_UNSUCCESS_RENT_COUNTER_LABEL = "MSA.BUSINESS.ORDER.UNSUCCESS.RENT.COUNTER";

    private final Counter createdCounter = Metrics.counter(ORDER_CREATED_COUNTER_LABEL);
    private final Counter successCounter = Metrics.counter(ORDER_SUCCESS_RENT_COUNTER_LABEL);
    private final Counter uunsuccessCounter = Metrics.counter(ORDER_UNSUCCESS_RENT_COUNTER_LABEL);

    public void logOrderCreated() {
        createdCounter.increment();
    }

    public void logOrderRentSuccess() {
        successCounter.increment();
    }

    public void logOrderRentUnSuccess() {
        uunsuccessCounter.increment();
    }
}
