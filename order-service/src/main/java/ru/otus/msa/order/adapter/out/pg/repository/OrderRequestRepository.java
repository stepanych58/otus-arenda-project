package ru.otus.msa.order.adapter.out.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.msa.order.adapter.out.pg.entity.OrderRequest;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRequestRepository extends JpaRepository<OrderRequest, UUID> {

    Optional<OrderRequest> findByRequestId(UUID requestId);
}