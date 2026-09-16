package dev.nelit.api.services.impl.payments.nowpayments;

import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.services.payments.PaymentGatewayStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NowPaymentsGatewayStrategy implements PaymentGatewayStrategy {

    private final NowPaymentsCheckoutServiceImpl nowPaymentsCheckoutService;

    @Override
    public PaymentGateway getType() {
        return PaymentGateway.NOWPAYMENTS;
    }

    @Override
    public Mono<String> createSession(PaymentResponse payment, CheckoutLineItem lineItem, Integer discountPercent) {
        return nowPaymentsCheckoutService.createSession(payment, lineItem, discountPercent);
    }
}
