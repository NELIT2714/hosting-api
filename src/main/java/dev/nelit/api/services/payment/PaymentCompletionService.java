package dev.nelit.api.services.payment;

import reactor.core.publisher.Mono;

public interface PaymentCompletionService {
    Mono<Void> complete(Long idPayment, String gatewayPaymentId);
    Mono<Void> fail(Long idPayment, String gatewayPaymentId);
}
