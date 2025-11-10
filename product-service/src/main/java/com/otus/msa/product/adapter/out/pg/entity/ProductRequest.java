package com.otus.msa.product.adapter.out.pg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "msa_product_request")
public class ProductRequest extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    public ProductRequest(UUID productId, UUID requestId) {
        this.productId = productId;
        this.requestId = requestId;
    }

    public ProductRequest() {
        super();
    }
}
