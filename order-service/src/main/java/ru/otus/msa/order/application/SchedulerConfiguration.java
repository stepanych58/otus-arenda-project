package ru.otus.msa.order.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SchedulerConfiguration {

    public static final String ORDER_SCHEDULER_BEAN_NAME = "ORDE_SCHEDULER_EXECUTOR_SERVICE";

    @Bean(value = ORDER_SCHEDULER_BEAN_NAME, destroyMethod = "shutdown")
    ExecutorService orderSchedulerExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
