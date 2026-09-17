package dev.nelit.api.services.impl.payment.balance;

import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.services.payment.PaymentCompletionService;
import dev.nelit.api.services.payment.PaymentGatewayStrategy;
import dev.nelit.api.services.user.UserBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BalanceGatewayStrategy implements PaymentGatewayStrategy {

    private final UserBalanceService userBalanceService;
    private final PaymentCompletionService paymentCompletionService;

    @Override
    public PaymentGateway getType() {
        return PaymentGateway.BALANCE;
    }

    @Override
    public Mono<String> createSession(PaymentResponse payment, CheckoutLineItem lineItem, Integer discountPercent) {
        return userBalanceService.deduct(payment.idUser(), lineItem.unitAmount())
            .then(paymentCompletionService.complete(payment.idPayment(), null))
            .then(Mono.empty());
    }
}
