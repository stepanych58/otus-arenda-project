package ru.otus.msa.order.adapter.in.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.msa.order.application.UserService;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

@Service
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;

    @KafkaListener(topics = {"user-event"}, containerFactory = "userEventContainerFactory")
    public void onMessage(UserEvent userEvent) {
        userService.createUser(userEvent);
    }
}
