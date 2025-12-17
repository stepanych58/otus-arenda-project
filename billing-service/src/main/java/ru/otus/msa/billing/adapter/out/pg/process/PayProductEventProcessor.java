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

import static ru.otus.msa.order.api.common.OrderStatus.WAITING_PAYMENT_PRODUCT;

@Service
@Log4j2
public class PayProductEventProcessor extends OrderEventProcessor {

    public PayProductEventProcessor(BillingService billingService) {
        super(billingService);
    }

    @Override
    public void process(OrderEvent orderEvent) {
        log.info("Запушен процесс оплаты продукта для orderEvent {}", orderEvent);
        UUID userId = orderEvent.userId();
        UUID managerId = orderEvent.managerId();
        UUID orderId = orderEvent.orderId();
        CurrencyEnum currency = CurrencyEnum.valueOf(orderEvent.currency().name());
        BigDecimal arendaCoast = orderEvent.getCoast();
        Account userAccount = billingService.getDebetAccount(userId, currency);
        if (userAccount.getBalance().compareTo(arendaCoast) < 0) {
            throw new RuntimeException("На счете недостаточно средств");
        }
        Account managerAccount = billingService.getDebetAccount(managerId, currency);
        userAccount.setBalance(userAccount.getBalance().subtract(arendaCoast));
        managerAccount.setBalance(managerAccount.getBalance().add(arendaCoast));
        PaymentEntity payment = billingService.createPayment(PaymentType.PRODUCT, orderId,
                userAccount.getId(),
                managerAccount.getId(),
                arendaCoast);
        billingService.createBillingEventOutbox(orderId, payment.getId(), BillingEventStatus.PRODUCT_PAID);
        billingService.saveAccount(List.of(managerAccount, userAccount));
    }

    @Override
    public List<OrderStatus> getStatus() {
        return List.of(WAITING_PAYMENT_PRODUCT);
    }

    @Override
    BillingEventStatus getFailedStatus() {
        return BillingEventStatus.PRODUCT_PAYMENT_REJECTED;
    }
}
