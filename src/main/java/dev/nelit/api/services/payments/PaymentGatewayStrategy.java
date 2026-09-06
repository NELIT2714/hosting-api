package dev.nelit.api.services.payments;

import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.enums.PaymentGateway;
import reactor.core.publisher.Mono;

public interface PaymentGatewayStrategy {
    PaymentGateway getType();
    Mono<String> createSession(PaymentResponse payment, CheckoutLineItem lineItem);
}
