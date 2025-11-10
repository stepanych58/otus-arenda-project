package ru.otus.msa.order.adapter.out.pg.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.otus.msa.order.adapter.out.pg.entity.OrderHistory;
import ru.otus.msa.order.api.common.OrderStatus;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, UUID> {
    List<OrderHistory> findAllByOrderIdOrderByCreatedAt(UUID orderId);

    List<OrderHistory> findAllByStatus(OrderStatus status, PageRequest pageble);

    List<OrderHistory> findAllByStatusAndSentFalse(OrderStatus status, PageRequest pageble);

    List<OrderHistory> findAllBySentFalse(PageRequest pageble);
}