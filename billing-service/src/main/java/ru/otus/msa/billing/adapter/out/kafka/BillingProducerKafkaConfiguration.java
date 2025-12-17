package ru.otus.msa.billing.adapter.out.kafka;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.otus.msa.user.api.kafka.dto.BillingEvent;

import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
class BillingProducerKafkaConfiguration {

    @Bean
    ProducerFactory<String, BillingEvent> producerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    KafkaTemplate<String, BillingEvent> kafkaTemplate(ProducerFactory<String, BillingEvent> producerFactory,
                                                      @Value("${billing-service.billing-order-event.topic-name:billing-order-event}") String orderEventTopic) {
        KafkaTemplate<String, BillingEvent> userEventKafkaTemplate = new KafkaTemplate<>(producerFactory);
        userEventKafkaTemplate.setDefaultTopic(orderEventTopic);
        return userEventKafkaTemplate;
    }

}
