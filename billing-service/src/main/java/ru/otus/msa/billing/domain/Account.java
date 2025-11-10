package ru.otus.msa.billing.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.otus.msa.user.api.CurrencyEnum;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "userId")
public class Account {
    UUID id;

    UUID userId;

    AccountType type;

    BigDecimal balance;

    CurrencyEnum currency;
}
