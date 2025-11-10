package ru.otus.msa.user.api.kafka.dto;

/**
 * Статусы для события о пользователе.
 */
public enum UserEventStatus {
    /**
     * Пользователь создан
     */
    CREATED,
    /**
     * Пользователь изменен
     */
    CHANGED,
    /**
     * Пользователь удален
     */
    DEACTIVATED
}
