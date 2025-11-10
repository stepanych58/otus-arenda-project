package ru.otus.msa.billing.adapter.out.pg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.msa.billing.adapter.out.pg.entity.AccountEntity;
import ru.otus.msa.billing.adapter.out.pg.entity.BillingOrderOutboxEntity;
import ru.otus.msa.billing.adapter.out.pg.entity.PaymentEntity;
import ru.otus.msa.billing.adapter.out.pg.entity.PaymentType;
import ru.otus.msa.billing.adapter.out.pg.repository.AccountEntityRepository;
import ru.otus.msa.billing.adapter.out.pg.repository.BillingOrderOutboxRepository;
import ru.otus.msa.billing.adapter.out.pg.repository.PaymentRepository;
import ru.otus.msa.billing.application.BillingService;
import ru.otus.msa.billing.application.exception.AccountNotFoundException;
import ru.otus.msa.billing.domain.Account;
import ru.otus.msa.billing.domain.AccountType;
import ru.otus.msa.user.api.BillingEventStatus;
import ru.otus.msa.user.api.CurrencyEnum;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingServiceImpl implements BillingService {

    private final AccountEntityRepository accountEntityRepository;

    private final AccountMapper accountMapper;

    private final BillingOrderOutboxRepository orderOutboxRepository;

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void createAccounts(UserEvent userEvent) {
        List<AccountEntity> existAccounts = accountEntityRepository.findByUserId(userEvent.userId());
        if (existAccounts.isEmpty()) {
            final var toSaveAccounts = accountMapper.mapUserEvents(userEvent);
            final var savedAccounts = accountEntityRepository.saveAll(toSaveAccounts);
            log.info("saved accounts: {}  for userId {}", savedAccounts, userEvent.userId());
        } else {
            log.info("accounts already exist for userId {}", userEvent.userId());
        }
    }

    @Override
    public Account getAccount(UUID userId, CurrencyEnum currency, AccountType accountType) {
        return accountEntityRepository.findByUserIdAndCurrencyAndType(
                        userId,
                        Optional.ofNullable(currency).orElse(CurrencyEnum.RUB),
                        accountType)
                .map(accountMapper::map)
                .orElseThrow(() -> new AccountNotFoundException(userId));
    }

    @Override
    public Account getDebetAccount(UUID userId, CurrencyEnum currency) {
        return getAccount(userId, currency, AccountType.DEBIT);
    }

    @Override
    public Account getDepositAccount(UUID userId, CurrencyEnum currency) {
        return getAccount(userId, currency, AccountType.DEPOSIT);
    }

    @Override
    public PaymentEntity createPayment(PaymentType paymentType, UUID orderId, UUID userAccountId,
                                       UUID managerAccountId, BigDecimal deposit) {
        PaymentEntity payment = new PaymentEntity();
        payment.setType(paymentType);
        payment.setOrderId(orderId);
        payment.setUserAccountId(userAccountId);
        payment.setManagerAccountId(managerAccountId);
        payment.setAmount(deposit);
        return paymentRepository.save(payment);
    }

    @Override
    public void createBillingEventOutbox(UUID orderId, UUID paymentId, BillingEventStatus depositPaid) {
        createBillingEventOutbox(orderId, paymentId, depositPaid, null);
    }

    @Override
    public List<Account> getAccounts(UUID userId, CurrencyEnum currency, AccountType accountType) {
        return accountEntityRepository.findByUserIdAndCurrencyAndType(userId,
                        Optional.ofNullable(currency).orElse(CurrencyEnum.RUB),
                        Optional.ofNullable(accountType).orElse(AccountType.DEBIT)
                )
                .stream()
                .map(accountMapper::map)
                .toList();
    }

    @Override
    public Account changeBalance(Account account) {
        return accountEntityRepository.findByUserIdAndCurrencyAndType(account.getUserId(),
                        Optional.ofNullable(account.getCurrency())
                                .orElse(CurrencyEnum.RUB),
                        AccountType.DEBIT)
                .stream()
                .findFirst()
                .map(a -> {
                    a.setBalance(a.getBalance().add(account.getBalance()));
                    return accountEntityRepository.save(a);
                })
                .map(accountMapper::map)
                .orElseThrow(() -> new AccountNotFoundException(account.getUserId()));
    }

    @Override
    public void createBillingEventOutbox(UUID orderId, UUID paymentId, BillingEventStatus depositPaid, String errorMessage) {
        BillingOrderOutboxEntity billingOrderOutboxEntity = new BillingOrderOutboxEntity();
        billingOrderOutboxEntity.setOrderId(orderId);
        billingOrderOutboxEntity.setPaymentId(paymentId);
        billingOrderOutboxEntity.setOrderId(orderId);
        billingOrderOutboxEntity.setStatus(depositPaid);
        billingOrderOutboxEntity.setSent(false);
        billingOrderOutboxEntity.setAttemptCount(0);
        billingOrderOutboxEntity.setErrorMessage(errorMessage);
        orderOutboxRepository.save(billingOrderOutboxEntity);
    }

    public void saveAccount(List<Account> accountEntities) {
        accountEntityRepository.saveAll(accountEntities.stream().map(
                accountMapper::toEntity
        ).toList());
    }
}