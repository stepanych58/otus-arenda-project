package ru.otus.msa.order.application;

import ru.otus.msa.user.api.kafka.dto.UserEvent;

public interface UserService {
    void createUser(UserEvent userEvent);
}
