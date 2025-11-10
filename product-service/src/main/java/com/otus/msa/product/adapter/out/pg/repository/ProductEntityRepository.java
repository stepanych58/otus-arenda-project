package com.otus.msa.product.adapter.out.pg.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;

public interface ProductEntityRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {
}