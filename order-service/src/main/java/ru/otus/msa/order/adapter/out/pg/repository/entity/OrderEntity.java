package ru.otus.msa.order.adapter.out.pg.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.otus.msa.order.api.common.OrderStatus;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "msa_order")
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID userId;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String rejectReason;
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<OrderItemEntity> items;
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;
}
