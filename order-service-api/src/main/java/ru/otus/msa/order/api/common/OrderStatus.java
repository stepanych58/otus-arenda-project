package ru.otus.msa.order.api.common;

import lombok.Getter;

/**
 * Статусы жизненного цикла заказа.
 */
public enum OrderStatus {

    /**
     * Ожидает оплаты
     */
    WAITING_PAYMENT,
    /**
     * Создан
     */
    CREATED(WAITING_PAYMENT),
    /**
     * Заказ отклонен во время проведения оплаты
     */
    PAYMENT_REJECT,
    /**
     * Резервирование продуктов
     */
    WAITING_RESERVE_PRODUCT,
    /**
     * Резервирование курьера
     */
    WAITING_RESERVE_DELIVERY,
    /**
     * Ожидание доставки курьером
     */
    WAITING_DELIVERY,
    /**
     * Заказ оплачен
     */
    PAYED(WAITING_RESERVE_PRODUCT),
    /**
     * Заказ отклонен во время резервирования продуктов
     */
    RESERVE_PRODUCT_REJECT,
    /**
     * Возврат оплаты
     */
    PAYMENT_REVERTED,
    /**
     * Резервирование продуктов выполнено
     */
    PRODUCT_RESERVED(WAITING_RESERVE_DELIVERY),

    /**
     * Заказ отклонен во время резервирования курьера
     */
    RESERVE_DELIVERY_REJECT,
    /**
     * Возврат зарезервированых продуктов
     */
    PRODUCT_REVERTED,

    /**
     * Резервирование курьера выполнено
     */
    DELIVERY_RESERVED(WAITING_DELIVERY),
    /**
     * Заказ завершен
     */
    COMPLETED;

    @Getter
    private OrderStatus next;

    OrderStatus() {
    }

    OrderStatus(OrderStatus next) {
        this.next = next;
    }
}
