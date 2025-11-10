package ru.otus.msa.billing.adapter.in.http;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.msa.billing.application.BillingService;
import ru.otus.msa.billing.domain.Account;
import ru.otus.msa.billing.domain.AccountType;
import ru.otus.msa.user.api.CurrencyEnum;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("billing/api/v1")
@RequiredArgsConstructor
public class AdminController {

    private final BillingService billingService;

    @PutMapping("/account/{uuid}/balance")
    public Account changeBalance(@PathVariable("uuid") UUID userId, @RequestBody Account account) {
        account.setUserId(userId);
        return billingService.changeBalance(account);
    }

    @GetMapping("/account/{uuid}")
    public List<Account> getAccount(@PathVariable("uuid") UUID userId,
                                    @RequestParam(value = "currency", required = false) CurrencyEnum currency,
                                    @RequestParam(value = "type", required = false) AccountType accountType) {
        return billingService.getAccounts(userId, currency, accountType);
    }
}
