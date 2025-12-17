package ru.otus.msa.order.adapter.out.pg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "msa_order_request")
public class OrderRequest extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "order_id", nullable = false)
    private UUID orderId;
    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    public OrderRequest(UUID orderId, UUID requestId) {
        this.orderId = orderId;
        this.requestId = requestId;
    }

    public OrderRequest() {
        super();
    }
}
