package ru.otus.msa.order.adapter.out.pg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.otus.msa.order.api.common.OrderStatus;

import java.util.UUID;

/**
 * Таблица, которая хранит историю изменения заказа.
 * Пока это только статус.
 * В дальнейшем можно добавить изменение состава заказа.
 */
@Getter
@Setter
@Entity
@Table(name = "order_history")
public class OrderHistory extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "sent")
    private Boolean sent;

    @Column(name = "sent_object")
    private String sentObject;
}
