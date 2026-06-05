package dev.nelit.api.services.impl.payments.stripe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import dev.nelit.api.config.StripeProperties;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.services.impl.VMServiceImpl;
import dev.nelit.api.services.impl.orders.VpsOrderServiceImpl;
import dev.nelit.api.services.payments.PaymentService;
import dev.nelit.api.services.payments.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StripeWebhookServiceImpl implements StripeWebhookService {

    private final StripeProperties stripeProperties;
    private final PaymentService paymentService;
    private final VMServiceImpl vmService;
    private final VpsOrderServiceImpl vpsOrderService;

    @Override
    public Mono<Void> handleEvent(String rawBody, String sigHeader) {
        return Mono.fromCallable(() -> Webhook.constructEvent(rawBody, sigHeader, stripeProperties.getWebhookSecret()))
            .flatMap(event -> switch (event.getType()) {
                case "checkout.session.completed" -> handleCheckoutCompleted(event);
                case "payment_intent.payment_failed" -> handlePaymentFailed(event);
                default -> Mono.empty();
            });
    }

    private Mono<Void> handleCheckoutCompleted(Event event) {
        JsonObject node = parseRawJson(event);
        Long idPayment = extractPaymentId(node);
        String gatewayPaymentId = extractField(node, "payment_intent");

        JsonObject metadata = node.getAsJsonObject("metadata");
        Long idUser = Long.parseLong(metadata.get("user_id").getAsString());

        return paymentService.update(idPayment, PaymentStatus.SUCCEEDED, gatewayPaymentId)
            .flatMap(_ -> vpsOrderService.getByIdPayment(idPayment))
            .flatMap(order -> vmService.create(idUser, order.getIdPlan(), order.getIdOsImage())
                .flatMap(vm -> vpsOrderService.setVm(order.getIdOrder(), vm.getIdVM()))
            )
            .then();
    }

    private Mono<Void> handlePaymentFailed(Event event) {
        JsonObject node = parseRawJson(event);
        Long idPayment = extractPaymentId(node);
        String gatewayPaymentId = extractField(node, "id");
        return paymentService.update(idPayment, PaymentStatus.FAILED, gatewayPaymentId).then();
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
