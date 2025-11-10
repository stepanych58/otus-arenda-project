package ru.otus.msa.order.adapter.in.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.msa.order.application.UserService;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserEventListener {

    private final UserService userService;

    @KafkaListener(topics = {"user-event"},
            containerFactory = "commonOrderFactory")
    public void onMessage(UserEvent userEvent) {
        log.info("Получено сообщение от сервиса user userEvent: {}", userEvent);
        userService.createUser(userEvent);
    }
}
