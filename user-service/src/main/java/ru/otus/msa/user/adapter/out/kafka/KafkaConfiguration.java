package ru.otus.msa.user.adapter.out.kafka;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

import java.util.Map;
import java.util.UUID;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
class KafkaConfiguration {

    @Bean
    ProducerFactory<String, UserEvent> producerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    KafkaTemplate<String, UserEvent> kafkaTemplate(ProducerFactory<String, UserEvent> producerFactory,
                                                 @Value("${user-service.user-event.topic-name:user-event}") String userEventTopic) {
        KafkaTemplate<String, UserEvent> userEventKafkaTemplate = new KafkaTemplate<>(producerFactory);
        userEventKafkaTemplate.setDefaultTopic(userEventTopic);
        return userEventKafkaTemplate;
    }

}
