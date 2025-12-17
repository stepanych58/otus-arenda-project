package ru.otus.msa.billing.application;

import ru.otus.msa.billing.adapter.out.pg.entity.PaymentEntity;
import ru.otus.msa.billing.adapter.out.pg.entity.PaymentType;
import ru.otus.msa.billing.domain.Account;
import ru.otus.msa.billing.domain.AccountType;
import ru.otus.msa.user.api.BillingEventStatus;
import ru.otus.msa.user.api.CurrencyEnum;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы со счетами пользователей
 */
public interface BillingService {
    /**
     * Метод создания счетов по userEvent
     *
     * @param account created user event
     */
    void createAccounts(UserEvent account);

    /**
     * Метод получаени информаүии о счете пользователя
     *
     * @param userId   ID Пользователя
     * @param currency
     * @return account
     */
    Account getAccount(UUID userId, CurrencyEnum currency, AccountType accountType);

    Account getDebetAccount(UUID userId, CurrencyEnum currency);

    Account getDepositAccount(UUID userId, CurrencyEnum currency);

    PaymentEntity createPayment(PaymentType paymentType, UUID orderId, UUID userId, UUID managerId, BigDecimal deposit);

    List<Account> getAccounts(UUID userId, CurrencyEnum currency, AccountType accountType);

    Account changeBalance(Account account);

    void createBillingEventOutbox(UUID orderId, UUID paymentId, BillingEventStatus status);

    void createBillingEventOutbox(UUID orderId, UUID paymentId, BillingEventStatus status, String message);

    void saveAccount(List<Account> accountEntities);
}
