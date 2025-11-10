package ru.otus.msa.order.adapter.out.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.msa.order.adapter.out.pg.repository.entity.OrderEntity;

import java.util.UUID;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, UUID> {

}