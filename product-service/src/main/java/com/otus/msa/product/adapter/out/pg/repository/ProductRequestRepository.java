package com.otus.msa.product.adapter.out.pg.repository;

import com.otus.msa.product.adapter.out.pg.entity.ProductRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRequestRepository extends JpaRepository<ProductRequest, UUID> {
    Optional<ProductRequest> findByRequestId(UUID requestId);
}