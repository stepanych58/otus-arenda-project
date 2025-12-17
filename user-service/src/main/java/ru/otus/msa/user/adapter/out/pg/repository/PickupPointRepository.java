package ru.otus.msa.user.adapter.out.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.otus.msa.user.adapter.out.pg.entity.PickupPoint;
import ru.otus.msa.user.adapter.out.pg.entity.User;

import java.util.UUID;

public interface PickupPointRepository extends JpaRepository<PickupPoint, UUID>, JpaSpecificationExecutor<User> {
}