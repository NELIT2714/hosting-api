package dev.nelit.api.services.payment;

import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.enums.PaymentType;
import reactor.core.publisher.Mono;

public interface PaymentFulfillmentHandler {
    PaymentType getSupportedType();
    Mono<Void> fulfill(PaymentResponse payment);
}
