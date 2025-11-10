package ru.otus.msa.billing.adapter.in.http.dto;

import java.math.BigDecimal;

/**
 * DTO продукта в заказе
 *
 * @param productName Наименование продукта
 * @param quantity    количество продуктов в заказе
 * @param itemPrice   стоимость продуктов
 */
public record OrderItemDto(String productName, Integer quantity, BigDecimal itemPrice) {

    /**
     * Общая стоимость orderItem
     *
     * @return -
     */
    public BigDecimal getCoast() {
        return itemPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
