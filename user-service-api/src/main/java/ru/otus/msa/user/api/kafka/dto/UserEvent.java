package ru.otus.msa.user.api.kafka.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import ru.otus.msa.user.api.common.PickupPointDto;

/**
 * Событие о изменениях связанных с пользователем.
 *
 * @param userId     ID пользователя
 * @param status     статус события
 * @param firstName  Имя пользователя
 * @param lastName   Фамилия пользователя
 * @param gender     гендер пользователя
 * @param birthDate  дата рождения пользователя
 * @param createdAt  дата когда пользователь был создан
 * @param modifiedAt дата последнего изменения пользователя
 */
@Builder
public record UserEvent(UUID userId,
                        UserEventStatus status,
                        String firstName,
                        String lastName,
                        Boolean gender,
                        Instant birthDate,
                        PickupPointDto pickupPoint,
                        Instant createdAt,
                        Instant modifiedAt
) {
}
