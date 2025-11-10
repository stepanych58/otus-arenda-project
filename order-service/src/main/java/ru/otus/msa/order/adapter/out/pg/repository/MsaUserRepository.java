package ru.otus.msa.order.adapter.out.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.msa.order.adapter.out.pg.repository.entity.MsaUserEntity;

import java.util.UUID;

public interface MsaUserRepository extends JpaRepository<MsaUserEntity, UUID> {
}