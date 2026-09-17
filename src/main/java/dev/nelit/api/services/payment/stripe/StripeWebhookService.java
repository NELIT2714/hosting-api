package dev.nelit.api.services.payment.stripe;

import reactor.core.publisher.Mono;

public interface StripeWebhookService {
    Mono<Void> handleEvent(String rawBody, String sigHeader);
}
