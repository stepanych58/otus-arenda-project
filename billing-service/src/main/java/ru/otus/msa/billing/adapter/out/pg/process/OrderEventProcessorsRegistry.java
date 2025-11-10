package ru.otus.msa.billing.adapter.out.pg.process;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.kafka.OrderEvent;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class OrderEventProcessorsRegistry {
    private final Map<OrderStatus, OrderEventProcessor> registry = new ConcurrentHashMap<>();

    public void register(OrderStatus status, OrderEventProcessor processor) {
        registry.put(status, processor);
    }

    public void process(OrderEvent orderEvent) {
        OrderEventProcessor orderEventProcessor = registry.get(orderEvent.status());
        if (Objects.isNull(orderEventProcessor)) {
            log.warn("не найден обработчик для события {}", orderEvent);
        } else {
            orderEventProcessor.process(orderEvent);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processException(OrderEvent orderEvent, Exception e) {
        registry.get(orderEvent.status())
                .processException(orderEvent, e);
    }
}
