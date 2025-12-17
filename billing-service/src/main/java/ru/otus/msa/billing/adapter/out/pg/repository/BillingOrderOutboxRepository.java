package ru.otus.msa.billing.adapter.out.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.msa.billing.adapter.out.pg.entity.BillingOrderOutboxEntity;

import java.util.UUID;

@Repository
public interface BillingOrderOutboxRepository extends JpaRepository<BillingOrderOutboxEntity, UUID> {
    Iterable<BillingOrderOutboxEntity> findBySentIsFalse();
}
