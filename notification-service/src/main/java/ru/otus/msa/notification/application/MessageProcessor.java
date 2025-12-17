package ru.otus.msa.notification.application;

import ru.otus.msa.notification.adapter.out.pg.entity.NotificationEntity;
import ru.otus.msa.order.api.kafka.OrderEvent;

public interface MessageProcessor {

    void process(NotificationEntity notificationEntity, OrderEvent orderEvent);

    String getContent(OrderEvent orderEvent);

    boolean isSupportedEventType(OrderEvent orderEvent);
}
