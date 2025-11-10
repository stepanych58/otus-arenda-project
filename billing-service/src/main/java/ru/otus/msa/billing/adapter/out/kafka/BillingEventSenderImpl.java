package ru.otus.msa.billing.adapter.out.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.otus.msa.billing.application.BillingEventSender;
import ru.otus.msa.user.api.kafka.dto.BillingEvent;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingEventSenderImpl implements BillingEventSender {

    private final KafkaTemplate<String, BillingEvent> kafkaTemplate;

    @Override
    public void send(BillingEvent billingEvent) throws ExecutionException, InterruptedException {
        String topicName = "billing-event";
        kafkaTemplate.send(topicName, billingEvent.orderId().toString(),
                        billingEvent)
                .whenComplete((res, err) ->
                        Optional.ofNullable(err)
                                .ifPresentOrElse(v -> log.error("Ошибка при попытке отправить billing event", v),
                                        () -> log.info("Отправка BillingEvent {} , -> {} ", billingEvent, topicName))
                )
                .get();
    }
}
