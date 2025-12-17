package ru.otus.msa.order.adapter.out.pg.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.otus.msa.order.adapter.out.pg.entity.OrderItemEntity;

public interface OrderItemEntityRepository extends JpaRepository<OrderItemEntity, UUID> {
}