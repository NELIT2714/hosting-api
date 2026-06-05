package dev.nelit.api.services.payments;

import reactor.core.publisher.Mono;

public interface StripeWebhookService {
    Mono<Void> handleEvent(String rawBody, String sigHeader);
}
