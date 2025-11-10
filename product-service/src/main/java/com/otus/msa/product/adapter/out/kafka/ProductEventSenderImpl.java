package com.otus.msa.product.adapter.out.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.otus.msa.product.api.kafka.dto.ProductEvent;
import com.otus.msa.product.api.kafka.dto.ProductOrderEvent;
import com.otus.msa.product.domain.ProductEventSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductEventSenderImpl implements ProductEventSender {

    private final KafkaTemplate<String, ProductOrderEvent> productOrderEventKafkaTemplate;

    private final KafkaTemplate<String, ProductEvent> productEventKafkaTemplate;

    @Override
    public void send(ProductOrderEvent event) {
        log.info("ОТПРАВКА ProductOrderEvent{{}} -> {}", event, productOrderEventKafkaTemplate.getDefaultTopic());
        productOrderEventKafkaTemplate.send("product-order-event", event);
    }

    @Override
    public void send(ProductEvent event) {
        log.info("ОТПРАВКА ProductEvent{{}} -> {}", event, productEventKafkaTemplate.getDefaultTopic());
        productEventKafkaTemplate.send("product-event", event);
    }
}
