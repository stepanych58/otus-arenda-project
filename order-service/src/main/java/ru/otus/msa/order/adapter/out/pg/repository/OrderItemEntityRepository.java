package ru.otus.msa.order.adapter.out.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.msa.order.adapter.out.pg.repository.entity.OrderItemEntity;

import java.util.UUID;

public interface OrderItemEntityRepository extends JpaRepository<OrderItemEntity, UUID> {
}