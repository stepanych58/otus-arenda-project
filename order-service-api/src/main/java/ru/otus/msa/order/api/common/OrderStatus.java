package ru.otus.msa.order.api.common;

import lombok.Getter;

/**
 * Статусы жизненного цикла заказа.
 */
public enum OrderStatus {

    /**
     * Заказ создан
     */
    CREATED,
    /**
     * Резервирование продуктов
     */
    WAITING_RESERVE_PRODUCT,
    /**
     * Ожидает оплаты залога
     */
    WAITING_PAYMENT_DEPOSIT,
    /**
     * Ожидание получения клиентом
     */
    WAITING_CLIENT_RECEIVE,

    /**
     * Ожидает оплаты аренды товара
     */
    WAITING_PAYMENT_PRODUCT,

    /**
     * Ожидание подтверждения администратора о получении
     */
    WAITING_ADMIN_RECEIVE,
    /**
     * Ожидание возврата депозита клиенту
     */
    WAITING_RETURN_DEPOSIT,

    /**
     * Ошибка во время возврата депозита
     */
    RETURN_DEPOSIT_ERROR,

    /**
     * Отказ при принятии администратором
     */
    ADMIN_RECEIVE_REJECT,

    /**
     * Клиент отказался при получении
     */
    WAITING_REVERT_RESERVE_PRODUCT,

    /**
     * Оплата товара отклонена
     */
    PAYMENT_PRODUCT_REJECT,
    /**
     * Клиент отказался от товара при получении
     */
    CLIENT_RECEIVE_REJECT,
    /**
     * Отклонен запрос на оплату депозита
     */
    PAYMENT_DEPOSIT_REJECT,
    /**
     * Отклонен запрос на бронирование товара
     */
    RESERVE_PRODUCT_REJECT,
    /**
     * Заказ не может быть выполнен.
     * Заказ отклонен.
     */
    REJECTED,
    /**
     * Заказ завершен
     */
    COMPLETED, RESERVE_PRODUCT_ERROR;

    @Getter
    private OrderStatus next;

    OrderStatus() {
    }

    OrderStatus(OrderStatus next) {
        this.next = next;
    }
}
