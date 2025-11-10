package ru.otus.msa.order.adapter.out.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.msa.order.adapter.out.pg.repository.entity.ProductEntity;

import java.util.UUID;

public interface ProductEntityRepository extends JpaRepository<ProductEntity, UUID> {
}