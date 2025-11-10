package ru.otus.msa.order.adapter.in.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.otus.msa.order.api.kafka.OrderEvent;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Configuration
public class OrderConsumersKafkaConfiguration {

    @Bean
    ConsumerFactory<String, UserEvent> userEventConsumerFactory(KafkaProperties kafkaProperties,
                                                                @Value("${spring.application.name}") String groupId) {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        properties.put(GROUP_ID_CONFIG, groupId);
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, UserEvent> userEventContainerFactory(
            ConsumerFactory<String, UserEvent> userEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, UserEvent> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(userEventConsumerFactory);
        return containerFactory;
    }

    @Bean
    ConsumerFactory<String, OrderEvent> orderEventConsumerFactory(KafkaProperties kafkaProperties,
                                                                  @Value("${spring.application.name}") String groupId) {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        properties.put(GROUP_ID_CONFIG, groupId);
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, OrderEvent> orderEventContainerFactory(
            ConsumerFactory<String, OrderEvent> orderEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(orderEventConsumerFactory);
        return containerFactory;
    }
}
