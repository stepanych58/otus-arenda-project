package com.otus.msa.product.adapter.out.pg.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.otus.msa.product.adapter.out.pg.entity.ProductReservationEntity;
import jakarta.validation.constraints.NotNull;

public interface ProductReservationEntityRepository extends JpaRepository<ProductReservationEntity, UUID> {
    void deleteAllByOrderIdAndUserId(@NotNull UUID orderId, @NotNull UUID userId);

    Optional<ProductReservationEntity> findByProductId(UUID firstProductId);

    List<ProductReservationEntity> findByOrderIdAndUserId(@NotNull UUID orderId, @NotNull UUID userId);
}