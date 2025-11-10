package ru.otus.msa.notification.adapter.out.pg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.msa.notification.adapter.out.pg.entity.NotificationEntity;
import ru.otus.msa.notification.adapter.out.pg.repository.NotificationEntityRepository;
import ru.otus.msa.notification.api.http.dto.NotificationDto;
import ru.otus.msa.notification.application.MessageProcessor;
import ru.otus.msa.notification.application.NotificationService;
import ru.otus.msa.order.api.kafka.OrderEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationEntityRepository notifications;

    private final NotificationMapper notificationMapper;

    private final MessageProcessor messageProcessor;

    @Override
    public List<NotificationDto> getNotifications(UUID userId, UUID orderId) {
        List<NotificationEntity> notificationList = notifications.findAllByUserId(userId);
        return notificationMapper.toDto(
                notificationList.stream()
                        .filter(no -> Objects.isNull(orderId) || Objects.equals(orderId, no.getOrderId()))
                        .toList());
    }

    @Override
    public void createNotification(OrderEvent orderEvent) {
        if (messageProcessor.isSupportedEventType(orderEvent)) {
            NotificationEntity notificationEntity = notificationMapper.toEntity(orderEvent);
            messageProcessor.process(notificationEntity, orderEvent);
            NotificationEntity notification = notifications.save(notificationEntity);
            log.info("Создано уведомление: {}", notification);
        } else {
            log.info("Не поддерживается обработка: {}", orderEvent.status());
        }
    }
}
