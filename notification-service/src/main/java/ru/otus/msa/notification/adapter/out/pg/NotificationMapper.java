package ru.otus.msa.notification.adapter.out.pg;

import java.util.List;
import org.mapstruct.Mapper;

import ru.otus.msa.notification.adapter.out.pg.entity.NotificationEntity;
import ru.otus.msa.notification.api.http.dto.NotificationDto;
import ru.otus.msa.order.api.kafka.OrderEvent;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    List<NotificationDto> toDto(List<NotificationEntity> entities);

    default NotificationEntity toEntity(OrderEvent orderEvent) {
        ;
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setUserId(orderEvent.userId());
        notificationEntity.setOrderId(orderEvent.orderId());
        return notificationEntity;
    }
}
