package ru.otus.msa.notification.adapter.out.pg.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.otus.msa.notification.adapter.out.pg.entity.NotificationEntity;

public interface NotificationEntityRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findAllByUserId(UUID userId);
}