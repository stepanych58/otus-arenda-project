package ru.otus.msa.order.adapter.out.pg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderServiceConfiguration {

    @Bean
    public ExecutorService createdOrderSender() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
