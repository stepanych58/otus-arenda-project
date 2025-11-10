package ru.otus.msa.billing.adapter.out.pg;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.msa.billing.adapter.out.pg.entity.AccountEntity;
import ru.otus.msa.billing.domain.Account;
import ru.otus.msa.billing.domain.AccountType;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "currency", constant = "RUB")
    @Mapping(target = "type", source = "accountType")
    AccountEntity mapUserEvent(UserEvent userEvent, AccountType accountType);

    default List<AccountEntity> mapUserEvents(UserEvent userEvent) {
        AccountEntity debitAccount = mapUserEvent(userEvent, AccountType.DEBIT);
        AccountEntity depositAccount = mapUserEvent(userEvent, AccountType.DEPOSIT);

        return List.of(debitAccount, depositAccount);
    }

    Account map(AccountEntity accountEntity);


    AccountEntity toEntity(Account account);
}
