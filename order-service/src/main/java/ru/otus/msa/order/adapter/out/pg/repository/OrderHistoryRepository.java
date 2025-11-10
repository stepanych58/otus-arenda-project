package ru.otus.msa.order.adapter.out.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.msa.order.adapter.out.pg.repository.entity.OrderHistory;

import java.util.List;
import java.util.UUID;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, UUID> {
    List<OrderHistory> findAllByOrderIdOrderByCreatedAt(UUID orderId);
}