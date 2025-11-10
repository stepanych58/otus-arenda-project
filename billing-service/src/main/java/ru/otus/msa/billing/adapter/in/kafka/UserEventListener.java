package ru.otus.msa.billing.adapter.in.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.msa.billing.application.BillingService;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

@Service
@RequiredArgsConstructor
public class UserEventListener {

    private final BillingService billingService;

    @KafkaListener(topics = {"user-event"}, containerFactory = "userEventContainerFactory")
    public void onMessage(UserEvent userEvent) {
        billingService.createAccounts(userEvent);
    }
}
