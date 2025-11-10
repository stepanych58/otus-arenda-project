package ru.otus.msa.billing.adapter.out.pg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.otus.msa.user.api.BillingEventStatus;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "billing_order_outbox")
public class BillingOrderOutboxEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BillingEventStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "sent")
    private Boolean sent;

    @Column(name = "attempt_count")
    private Integer attemptCount;
}
