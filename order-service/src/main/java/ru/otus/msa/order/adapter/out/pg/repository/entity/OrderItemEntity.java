package ru.otus.msa.order.adapter.out.pg.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "order_item")
public class OrderItemEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
    //    @ManyToOne
//    @JoinColumn(name = "product_id")
    private UUID productId;
    private Integer quantity;
    private BigDecimal price;
}
