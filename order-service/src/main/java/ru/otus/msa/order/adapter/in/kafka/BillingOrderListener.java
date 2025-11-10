package ru.otus.msa.order.adapter.in.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.msa.order.adapter.out.pg.entity.OrderEntity;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.application.OrderService;
import ru.otus.msa.user.api.BillingEventStatus;
import ru.otus.msa.user.api.kafka.dto.BillingEvent;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class BillingOrderListener {

    private final OrderService orderService;

    @KafkaListener(topics = {"billing-event"},
            containerFactory = "commonOrderFactory")
    public void onMessage(BillingEvent billingEvent) {
        log.info("Получено сообщение от сервиса billing: {}", billingEvent);
        orderService.updateByBillingEvent(billingEvent);
    }
}
