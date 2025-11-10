package ru.otus.msa.order.adapter.out.pg.repository.entity;

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
    private String name;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;
}
