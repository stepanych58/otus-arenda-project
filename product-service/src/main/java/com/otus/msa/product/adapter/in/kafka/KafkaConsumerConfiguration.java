package com.otus.msa.product.adapter.in.kafka;

import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import org.springframework.messaging.Message;
import ru.otus.msa.order.api.kafka.OrderEvent;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Configuration
@Log4j2
public class KafkaConsumerConfiguration {

    @Bean
    ConsumerFactory<String, OrderEvent> orderEventConsumerFactory(KafkaProperties kafkaProperties,
                                                                  @Value("${spring.application.name}") String groupId) {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        properties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, OrderEvent> orderEventContainerFactory(
            ConsumerFactory<String, OrderEvent> orderEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(orderEventConsumerFactory);
        containerFactory.setCommonErrorHandler(new CommonLoggingErrorHandler());
        return containerFactory;
    }

    @Bean
    public KafkaListenerErrorHandler defaultKafkaErrorHandler() {
        // TODO: add correct error handling
        return new KafkaListenerErrorHandler() {
            @Override
            public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
                log.error("Got bad message from Kafka. message {}, exception: {}", message, exception);
                return null;
            }

            @Override
            public Object handleError(Message<?> message, ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {
                return handleError(message, exception);
            }
        };
    }

}
