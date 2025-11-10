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

import static ru.otus.msa.order.api.common.OrderStatus.WAITING_RETURN_DEPOSIT;

@Component
@Log4j2
public class DepositReturnOrderEventProcessor extends OrderEventProcessor {

    public DepositReturnOrderEventProcessor(BillingService billingService) {
        super(billingService);
    }

    @Override
    @Transactional
    public void process(OrderEvent orderEvent) {
        log.info("Запушен процесс возврата депозита для orderEvent {}", orderEvent);
        //найти счет клиента, снять деньги,
        //найти депозитарный счет менеджера положить деньги
        //сформировать платеж от кого кому по какому заказу

        CurrencyEnum currency = CurrencyEnum.valueOf(orderEvent.currency().name());
        UUID userId = orderEvent.userId();
        UUID managerId = orderEvent.managerId();
        BigDecimal deposit = orderEvent.getDeposit();
        Account managerAccount = billingService.getDepositAccount(managerId, currency);
        managerAccount.setBalance(managerAccount.getBalance().subtract(deposit));
        Account userAccount = billingService.getDebetAccount(userId, currency);
        userAccount.setBalance(userAccount.getBalance().add(deposit));
        UUID orderId = orderEvent.orderId();
        PaymentEntity payment = billingService.createPayment(PaymentType.RETURN_CLIENT_DEPOSIT,
                orderId,
                userAccount.getId(),
                managerAccount.getId(),
                deposit);
        billingService.createBillingEventOutbox(orderId, payment.getId(), BillingEventStatus.DEPOSIT_RETURNED);
        billingService.saveAccount(List.of(managerAccount, userAccount));
    }

    @Override
    public List<OrderStatus> getStatus() {
        return List.of(WAITING_RETURN_DEPOSIT);
    }

    @Override
    BillingEventStatus getFailedStatus() {
        return BillingEventStatus.DEPOSIT_RETURN_REJECT;
    }
}
