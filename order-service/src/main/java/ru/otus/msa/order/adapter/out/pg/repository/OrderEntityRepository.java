package ru.otus.msa.order.adapter.out.pg.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.otus.msa.order.adapter.out.pg.entity.OrderEntity;
import ru.otus.msa.order.api.common.OrderStatus;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findAllByStatus(OrderStatus orderStatus, Pageable pageable);

    Integer countByUser_Id(UUID id);
}