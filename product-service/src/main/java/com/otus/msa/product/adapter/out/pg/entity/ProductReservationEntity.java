package com.otus.msa.product.adapter.out.pg.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Зарезервированные продукты
 */
@Getter
@Setter
@Entity
@Table(name = "product_reservation")
public class ProductReservationEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID productId;

    private UUID orderId;

    private UUID userId;

    private Integer count;
}
