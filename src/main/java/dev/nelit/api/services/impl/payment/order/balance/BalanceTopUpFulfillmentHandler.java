package dev.nelit.api.services.impl.payment.order.balance;

import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.enums.PaymentType;
import dev.nelit.api.services.payment.PaymentFulfillmentHandler;
import dev.nelit.api.services.user.UserBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BalanceTopUpFulfillmentHandler implements PaymentFulfillmentHandler {

    private final UserBalanceService userBalanceService;

    @Override
    public PaymentType getSupportedType() {
        return PaymentType.BALANCE_TOPUP;
    }

    @Override
    public Mono<Void> fulfill(PaymentResponse payment) {
        return userBalanceService.credit(payment.idUser(), payment.amount());
    }
}
