package dev.nelit.api.services.impl.payments.stripe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import dev.nelit.api.config.StripeProperties;
import dev.nelit.api.services.payments.PaymentCompletionService;
import dev.nelit.api.services.payments.stripe.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StripeWebhookServiceImpl implements StripeWebhookService {

    private final StripeProperties stripeProperties;
    private final PaymentCompletionService paymentCompletionService;

    @Override
    public Mono<Void> handleEvent(String rawBody, String sigHeader) {
        return Mono.fromCallable(() -> Webhook.constructEvent(rawBody, sigHeader, stripeProperties.getWebhookSecret()))
            .flatMap(event -> switch (event.getType()) {
                case "checkout.session.completed" -> handleCompleted(event);
                case "payment_intent.payment_failed" -> handleFailed(event);
                default -> Mono.empty();
            });
    }

    private Mono<Void> handleCompleted(Event event) {
        JsonObject node = parseRawJson(event);
        return paymentCompletionService.complete(extractPaymentId(node), extractField(node, "payment_intent"));
    }

    private Mono<Void> handleFailed(Event event) {
        JsonObject node = parseRawJson(event);
        return paymentCompletionService.fail(extractPaymentId(node), extractField(node, "id"));
    }

    private JsonObject parseRawJson(Event event) {
        return JsonParser.parseString(event.getDataObjectDeserializer().getRawJson()).getAsJsonObject();
    }

    private Long extractPaymentId(JsonObject node) {
        JsonObject metadata = node.has("metadata") ? node.getAsJsonObject("metadata") : null;
        if (metadata == null || !metadata.has("payment_id")) {
            throw new RuntimeException("payment_id missing in metadata");
        }
        return Long.parseLong(metadata.get("payment_id").getAsString());
    }

    private String extractField(JsonObject node, String field) {
        if (!node.has(field)) {
            throw new RuntimeException(field + " missing in event");
        }
        return node.get(field).getAsString();
    }
}
