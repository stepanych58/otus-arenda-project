package ru.otus.msa.billing.adapter.out.pg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.otus.msa.billing.domain.AccountType;
import ru.otus.msa.user.api.CurrencyEnum;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "account")
public class AccountEntity extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;//т.к. под разные валөты будут разные аккауны/счета

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;

    public void setBalance(BigDecimal balance) {
        this.balance = Optional.ofNullable(balance)
                .orElse(BigDecimal.ZERO);
    }

}
