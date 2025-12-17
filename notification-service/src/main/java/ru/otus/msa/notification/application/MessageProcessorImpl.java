package ru.otus.msa.notification.application;

import org.springframework.stereotype.Component;
import ru.otus.msa.notification.adapter.out.pg.entity.NotificationEntity;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.kafka.OrderEvent;

import java.util.Map;

import static ru.otus.msa.order.api.common.OrderStatus.*;

@Component
public class MessageProcessorImpl implements MessageProcessor {

    static final ContentProcessor CREATED_ORDER_CONTENT_PROCESSOR = new CreatedOrderContentProcessor();
    static final ContentProcessor ORDER_READY_PROCESSOR = new OrderReadyProcessor();
    static final ContentProcessor ORDER_RETURN_PROCESSOR = new OrderReturnProcessor();
    static final ContentProcessor COMPLETE_ORDER_CONTENT_PROCESSOR = new CompleteOrderContentProcessor();
    static final ContentProcessor REJECT_ORDER_CONTENT_PROCESSOR = new RejectOrderContentProcessor();

    private static final Map<OrderStatus, ContentProcessor> CONTENT_PROCESSOR_MAP = Map.of(
            CREATED, CREATED_ORDER_CONTENT_PROCESSOR,
            WAITING_CLIENT_RECEIVE, ORDER_READY_PROCESSOR,
            WAITING_ADMIN_RECEIVE, ORDER_RETURN_PROCESSOR,
            COMPLETED, COMPLETE_ORDER_CONTENT_PROCESSOR,
            REJECTED, REJECT_ORDER_CONTENT_PROCESSOR
    );

    @Override
    public void process(NotificationEntity notificationEntity, OrderEvent orderEvent) {
        String messageContent = getContent(orderEvent);
        notificationEntity.setContent(messageContent);
    }

    @Override
    public String getContent(OrderEvent orderEvent) {
        return CONTENT_PROCESSOR_MAP.get(orderEvent.status())
                .getMessageContent(orderEvent);
    }

    interface ContentProcessor {
        String getMessageContent(OrderEvent orderEvent);
    }

    static class CreatedOrderContentProcessor implements ContentProcessor {
        @Override
        public String getMessageContent(OrderEvent orderEvent) {
            return NotificationTemplates.createOrderCreatedMessage(
                    orderEvent.orderName()
            );
        }
    }

    static class OrderReadyProcessor implements ContentProcessor {
        @Override
        public String getMessageContent(OrderEvent orderEvent) {
            return NotificationTemplates.createOrderReadyMessage(
                    orderEvent.orderName(), orderEvent.rentStartDate(),
                    orderEvent.pickUpAddress(), orderEvent.pickUpTimes()
            );
        }
    }

    static class OrderReturnProcessor implements ContentProcessor {
        @Override
        public String getMessageContent(OrderEvent orderEvent) {
            return NotificationTemplates.createOrderToReturnMessage(
                    orderEvent.orderName(), orderEvent.rentCompleteDate(),
                    orderEvent.pickUpAddress(), orderEvent.pickUpTimes()
            );
        }
    }

    static class CompleteOrderContentProcessor implements ContentProcessor {
        @Override
        public String getMessageContent(OrderEvent orderEvent) {
            return NotificationTemplates.createOrderCompleteMessage(orderEvent.orderName());
        }
    }

    static class RejectOrderContentProcessor implements ContentProcessor {
        @Override
        public String getMessageContent(OrderEvent orderEvent) {
            return NotificationTemplates.createOrderRejectMessage(orderEvent.orderName(),
                    orderEvent.rejectReason());
        }
    }

    @Override
    public boolean isSupportedEventType(OrderEvent orderEvent) {
        return CONTENT_PROCESSOR_MAP.containsKey(orderEvent.status());
    }
}
