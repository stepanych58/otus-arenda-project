package ru.otus.msa.billing.adapter.out.pg.process;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
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

import static ru.otus.msa.order.api.common.OrderStatus.ADMIN_RECEIVE_REJECT;
import static ru.otus.msa.order.api.common.OrderStatus.CLIENT_RECEIVE_REJECT;

@Service
@Log4j2
public class PayDepositToManagerEventProcessor extends OrderEventProcessor {

    public PayDepositToManagerEventProcessor(BillingService billingService) {
        super(billingService);
    }

    @Override
    public void process(OrderEvent orderEvent) {
        log.info("Запушен процесс перевода депозита менеджеру orderEvent {}", orderEvent);
        UUID userId = orderEvent.userId();
        UUID managerId = orderEvent.managerId();
        UUID orderId = orderEvent.orderId();
        CurrencyEnum currency = CurrencyEnum.valueOf(orderEvent.currency().name());
        BigDecimal orderEventDeposit = orderEvent.getDeposit();
        Account managerDebet = billingService.getDebetAccount(managerId, currency);
        Account managerDeposit = billingService.getDepositAccount(managerId, currency);
        managerDebet.setBalance(managerDebet.getBalance().add(orderEventDeposit));
        managerDeposit.setBalance(managerDebet.getBalance().subtract(orderEventDeposit));
        PaymentEntity payment = billingService.createPayment(PaymentType.DEPOSIT_MANAGER_ACCRUED, orderId,
                userId,
                managerId,
                orderEventDeposit);
        billingService.createBillingEventOutbox(orderId, payment.getId(), BillingEventStatus.DEPOSIT_MANAGER_ACCRUED);
        billingService.saveAccount(List.of(managerDebet, managerDeposit));
    }

    @Override
    public List<OrderStatus> getStatus() {
        return List.of(CLIENT_RECEIVE_REJECT, ADMIN_RECEIVE_REJECT);
    }

    @Override
    BillingEventStatus getFailedStatus() {
        return BillingEventStatus.DEPOSIT_MANAGER_ACCRUED_ERROR;
    }
}
