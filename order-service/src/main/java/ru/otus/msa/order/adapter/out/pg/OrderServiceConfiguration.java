package ru.otus.msa.order.adapter.out.pg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class OrderServiceConfiguration {

    @Bean
    public ExecutorService createdOrderSender() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
