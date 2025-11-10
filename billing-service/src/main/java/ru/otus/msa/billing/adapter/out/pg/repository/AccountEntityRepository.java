package ru.otus.msa.billing.adapter.out.pg.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.msa.billing.adapter.out.pg.entity.AccountEntity;
import ru.otus.msa.billing.domain.AccountType;
import ru.otus.msa.user.api.CurrencyEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountEntityRepository extends CrudRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByUserIdAndCurrencyAndType(UUID userId,
                                                           CurrencyEnum currency,
                                                           AccountType type);

    List<AccountEntity> findByUserIdAndCurrency(UUID userId,
                                                CurrencyEnum currency);

    List<AccountEntity> findByUserId(UUID userId);
}
