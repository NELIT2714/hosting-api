package dev.nelit.api.services.payment.nowpayments;

import reactor.core.publisher.Mono;

public interface NowPaymentsWebhookService {
    Mono<Void> handleIpn(String rawBody, String signatureHeader);
}
