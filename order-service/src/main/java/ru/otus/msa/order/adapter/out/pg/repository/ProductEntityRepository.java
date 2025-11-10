package ru.otus.msa.order.adapter.out.pg.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.otus.msa.order.adapter.out.pg.entity.ProductEntity;

public interface ProductEntityRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findAllByProductIdIn(List<UUID> productIds);

    Optional<ProductEntity> findByProductId(UUID productId);
}