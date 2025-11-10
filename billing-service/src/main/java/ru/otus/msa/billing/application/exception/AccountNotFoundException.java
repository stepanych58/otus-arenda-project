package ru.otus.msa.billing.application.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID userId) {
        super(String.format("Не найден акаунт %s", userId));
    }
}
