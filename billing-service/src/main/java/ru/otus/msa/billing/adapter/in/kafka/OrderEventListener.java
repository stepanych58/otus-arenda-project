package ru.otus.msa.billing.adapter.in.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.msa.billing.adapter.out.pg.process.OrderEventProcessorsRegistry;
import ru.otus.msa.order.api.kafka.OrderEvent;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderEventListener {

    private final OrderEventProcessorsRegistry orderProcessors;

    @KafkaListener(topics = {"order-event"},
            containerFactory = "orderEventContainerFactory")
    public void onMessage(OrderEvent orderEvent) {
        log.info("Получено сообщение orderEvent:{}", orderEvent);
        try {
            orderProcessors.process(orderEvent);
        } catch (Exception e) {
            log.error("Ошибка при обработке orderEvent:{}", orderEvent, e);
            orderProcessors.processException(orderEvent, e);
        }
    }
}
