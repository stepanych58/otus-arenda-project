package ru.otus.msa.notification.application;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public final class NotificationTemplates {

    public static final String ORDER_CREATED_TEMPLATE =
            """
                    Ваш заказ %s создан""";

    public static final String ORDER_READY_TO_RECEIVE_TEMPLATE =
            """
                    Ваш заказ %s принят,
                    приходите %s числа по адресу %s.
                    Время работы %s""";

    public static final String ORDER_WAITING_TO_RETURN_TEMPLATE =
            """
                    Ожидаем возврата по заказу %s, до %s.
                    Возврат по адресу %s.
                    Время работы %s""";

    public static final String ORDER_COMPLETED_TEMPLATE =
            """
                    Заказ %s завершен.
                    Спасибо за ваше доверие.
                    Приходите ещё!""";

    public static final String ORDER_REJECT_TEMPLATE =
            """
                    К сожалению заказ %s отменен.
                    %s
                    Пожалуйста обратитесь в службу поддержки 8-800-888-88-88
                    """;

    public static String createOrderCreatedMessage(String orderName) {
        return String.format(ORDER_CREATED_TEMPLATE, orderName);
    }

    public static String createOrderReadyMessage(String orderName, String rentStartDate,
                                                 String pickUpAddress, String pickUpTimes) {
        return String.format(ORDER_READY_TO_RECEIVE_TEMPLATE, orderName, rentStartDate, pickUpAddress, pickUpTimes);
    }

    public static String createOrderToReturnMessage(String orderName, String rentCompleteDate,
                                                    String pickUpAddress, String pickUpTimes) {
        return String.format(ORDER_WAITING_TO_RETURN_TEMPLATE, orderName, rentCompleteDate, pickUpAddress, pickUpTimes);
    }

    public static String createOrderCompleteMessage(String orderName) {
        return String.format(ORDER_COMPLETED_TEMPLATE, orderName);
    }

    public static String createOrderRejectMessage(String orderName, String rejectReason) {
        String rejectReasonStr = StringUtils.hasText(rejectReason) ?
                String.format("Возможная причина отмены: %s.", rejectReason) : "";
        return String.format(ORDER_REJECT_TEMPLATE, orderName, rejectReasonStr);
    }

}
