package ru.otus.msa.billing.adapter.out.pg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class PaymentEntity extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "manager_account_id")
    private UUID managerAccountId;

    @Column(name = "user_account_id")
    private UUID userAccountId;

    @Column(name = "amount")
    private BigDecimal amount;

}
