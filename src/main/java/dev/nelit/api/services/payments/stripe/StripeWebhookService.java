package dev.nelit.api.services.payments.stripe;

import reactor.core.publisher.Mono;

public interface StripeWebhookService {
    Mono<Void> handleEvent(String rawBody, String sigHeader);
}
