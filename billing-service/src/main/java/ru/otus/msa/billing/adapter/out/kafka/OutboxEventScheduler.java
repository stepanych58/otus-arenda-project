package ru.otus.msa.billing.adapter.out.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.otus.msa.billing.adapter.out.pg.repository.BillingOrderOutboxRepository;
import ru.otus.msa.billing.application.BillingEventSender;
import ru.otus.msa.user.api.kafka.dto.BillingEvent;

import static ru.otus.msa.billing.adapter.out.kafka.SchedulerConfiguration.BILLING_SCHEDULER_BEAN_NAME;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutboxEventScheduler {

    private final BillingOrderOutboxRepository outboxRepository;

    private final BillingEventSender billingEventSender;

    private final BillingEventMapper billingEventMapper;

    @Scheduled(fixedRateString = "${billing-service.outbox-fixed-rate:1000}")
    @Async(BILLING_SCHEDULER_BEAN_NAME)
    public void process() {
        outboxRepository.findBySentIsFalse()
                .forEach(outbox -> {
                    try {
                        BillingEvent billingEvent = billingEventMapper.toEvent(outbox);
                        billingEventSender.send(billingEvent);
                        outbox.setSent(true);
                        outbox.setAttemptCount(outbox.getAttemptCount() + 1);
                        outboxRepository.save(outbox);
                    } catch (Exception e) {
                        log.error("OutboxEventScheduler", e);
                        outbox.setSent(false);
                        outboxRepository.save(outbox);
                    }
                });
    }
}
