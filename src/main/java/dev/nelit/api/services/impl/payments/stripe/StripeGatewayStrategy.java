package dev.nelit.api.services.impl.payments.stripe;

import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.services.payments.PaymentGatewayStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StripeGatewayStrategy implements PaymentGatewayStrategy {

    private final StripeCheckoutServiceImpl stripeCheckoutService;

    @Override
    public PaymentGateway getType() {
        return PaymentGateway.STRIPE;
    }

    @Override
    public Mono<String> createSession(PaymentResponse payment, CheckoutLineItem lineItem) {
        return stripeCheckoutService.createSession(payment, lineItem);
    }
}
