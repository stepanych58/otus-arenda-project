package com.otus.msa.product.adapter.out.kafka;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Map;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.otus.msa.product.api.kafka.dto.ProductEvent;
import com.otus.msa.product.api.kafka.dto.ProductOrderEvent;

@Configuration
class ProductProducerKafkaConfiguration {

    @Bean
    ProducerFactory<String, ProductOrderEvent> producerProductOrderFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    KafkaTemplate<String, ProductOrderEvent> productOrderEventKafkaTemplate(ProducerFactory<String, ProductOrderEvent> producerFactory,
                                                                            @Value("${product-service.product-event.topic-name:product-order-event}")
                                                                            String productEventTopic) {
        KafkaTemplate<String, ProductOrderEvent> userEventKafkaTemplate = new KafkaTemplate<>(producerFactory);
        userEventKafkaTemplate.setDefaultTopic(productEventTopic);
        return userEventKafkaTemplate;
    }

    @Bean
    ProducerFactory<String, ProductEvent> productEventProducerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    KafkaTemplate<String, ProductEvent> productEventKafkaTemplate(ProducerFactory<String, ProductEvent> producerFactory,
                                                                  @Value("${product-service.product-event.topic-name:product-event}")
                                                                  String productEventTopic) {
        KafkaTemplate<String, ProductEvent> userEventKafkaTemplate = new KafkaTemplate<>(producerFactory);
        userEventKafkaTemplate.setDefaultTopic(productEventTopic);
        return userEventKafkaTemplate;
    }

}
