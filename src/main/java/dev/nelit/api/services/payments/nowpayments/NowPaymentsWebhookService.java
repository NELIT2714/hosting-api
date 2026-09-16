package dev.nelit.api.services.payments.nowpayments;

import reactor.core.publisher.Mono;

public interface NowPaymentsWebhookService {
    Mono<Void> handleIpn(String rawBody, String signatureHeader);
}
