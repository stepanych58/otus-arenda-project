package ru.otus.msa.billing.adapter.out.pg.process;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.msa.billing.adapter.out.pg.entity.PaymentEntity;
import ru.otus.msa.billing.adapter.out.pg.entity.PaymentType;
import ru.otus.msa.billing.application.BillingService;
import ru.otus.msa.billing.domain.Account;
import ru.otus.msa.order.api.common.OrderStatus;
import ru.otus.msa.order.api.kafka.OrderEvent;
import ru.otus.msa.user.api.BillingEventStatus;
import ru.otus.msa.user.api.CurrencyEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static ru.otus.msa.order.api.common.OrderStatus.WAITING_PAYMENT_DEPOSIT;

@Component
@Log4j2
public class DepositOrderEventProcessor extends OrderEventProcessor {

    public DepositOrderEventProcessor(BillingService billingService) {
        super(billingService);
    }

    @Override
    @Transactional
    public void process(OrderEvent orderEvent) {
        log.info("Запушен процесс оплаты депозита для orderEvent {}", orderEvent);
        //найти депозитарный счет менеджера снять деньги
        //найти счет клиента положить деньги
        //сформировать платеж от кого, кому, по какому заказу
        //записать аутбокс
        CurrencyEnum currency = CurrencyEnum.valueOf(orderEvent.currency().name());
        UUID userId = orderEvent.userId();
        UUID managerId = orderEvent.managerId();
        BigDecimal deposit = orderEvent.getDeposit();
        Account userAccount = billingService.getDebetAccount(userId, currency);
        if (userAccount.getBalance().compareTo(deposit) < 0) {
            throw new RuntimeException("На счете недостаточно средств");
        }
        userAccount.setBalance(userAccount.getBalance().subtract(deposit));
        Account managerAccount = billingService.getDepositAccount(managerId, currency);
        managerAccount.setBalance(managerAccount.getBalance().add(deposit));
        UUID orderId = orderEvent.orderId();
        PaymentEntity payment = billingService.createPayment(PaymentType.CLIENT_DEPOSIT, orderId,
                userAccount.getId(),
                managerAccount.getId(), deposit);
        billingService.createBillingEventOutbox(orderId, payment.getId(), BillingEventStatus.DEPOSIT_PAID);
        billingService.saveAccount(List.of(managerAccount, userAccount));
    }

    @Override
    public List<OrderStatus> getStatus() {
        return List.of(WAITING_PAYMENT_DEPOSIT);
    }

    @Override
    public BillingEventStatus getFailedStatus() {
        return BillingEventStatus.DEPOSIT_PAYMENT_REJECTED;
    }

}
