package ru.otus.msa.order.adapter.out.pg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product")
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "deposit_sum")
    private BigDecimal depositSum;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;
}
