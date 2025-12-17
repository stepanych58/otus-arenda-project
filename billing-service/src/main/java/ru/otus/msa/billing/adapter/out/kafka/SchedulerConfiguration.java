package ru.otus.msa.billing.adapter.out.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SchedulerConfiguration {

    public static final String BILLING_SCHEDULER_BEAN_NAME = "BILLING_SCHEDULER_EXECUTOR_SERVICE";

    @Bean(value = BILLING_SCHEDULER_BEAN_NAME, destroyMethod = "shutdown")
    ExecutorService billingSchedulerExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
