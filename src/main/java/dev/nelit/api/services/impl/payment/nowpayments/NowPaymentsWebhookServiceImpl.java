package dev.nelit.api.services.impl.payment.nowpayments;

import dev.nelit.api.config.nowpayments.NowPaymentsProperties;
import dev.nelit.api.domain.exception.nowpayments.InvalidNowPaymentsSignatureException;
import dev.nelit.api.dto.nowpayments.NowPaymentsIpnPayload;
import dev.nelit.api.services.payment.PaymentCompletionService;
import dev.nelit.api.services.payment.nowpayments.NowPaymentsWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Set;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NowPaymentsWebhookServiceImpl implements NowPaymentsWebhookService {

    private static final Set<String> SUCCESS_STATUSES = Set.of("finished");
    private static final Set<String> FAILURE_STATUSES = Set.of("failed", "expired", "refunded");

    private final NowPaymentsProperties properties;
    private final PaymentCompletionService paymentCompletionService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handleIpn(String rawBody, String signatureHeader) {
        return Mono.fromCallable(() -> verifyAndParse(rawBody, signatureHeader))
            .flatMap(payload -> {
                String status = payload.paymentStatus();
                Long idPayment = Long.parseLong(payload.orderId());
                String gatewayPaymentId = String.valueOf(payload.paymentId());

                if (SUCCESS_STATUSES.contains(status)) {
                    return paymentCompletionService.complete(idPayment, gatewayPaymentId);
                }
                if (FAILURE_STATUSES.contains(status)) {
                    return paymentCompletionService.fail(idPayment, gatewayPaymentId);
                }
                // waiting / confirming / confirmed / sending / partially_paid — промежуточные, игнорируем
                log.info("NowPayments IPN: ignored intermediate status '{}' for payment {}", status, idPayment);
                return Mono.empty();
            });
    }

    private NowPaymentsIpnPayload verifyAndParse(String rawBody, String signatureHeader) {
        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new InvalidNowPaymentsSignatureException();
        }

        String expectedSignature = computeSignature(rawBody);
        if (!MessageDigest.isEqual(
            expectedSignature.getBytes(StandardCharsets.UTF_8),
            signatureHeader.getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidNowPaymentsSignatureException();
        }

        log.info("Computed signature: {}", expectedSignature);
        log.info("Received signature: {}", signatureHeader);

        try {
            return objectMapper.readValue(rawBody, NowPaymentsIpnPayload.class);
        } catch (Exception e) {
            throw new InvalidNowPaymentsSignatureException();
        }
    }

    private String computeSignature(String rawBody) {
        try {
            ObjectMapper bigDecimalMapper = objectMapper.rebuild()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .build();

            JsonNode sorted = sortNode(bigDecimalMapper.readTree(rawBody));
            String sortedJson = bigDecimalMapper.writeValueAsString(sorted);

            log.info("Sorted JSON for signature: {}", sortedJson);

            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(properties.getIpnSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] hash = mac.doFinal(sortedJson.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new InvalidNowPaymentsSignatureException();
        }
    }

    private JsonNode sortNode(JsonNode node) {
        if (node.isObject()) {
            TreeMap<String, JsonNode> sortedFields = new TreeMap<>();
            node.properties().forEach(e -> sortedFields.put(e.getKey(), sortNode(e.getValue())));
            ObjectNode result = objectMapper.createObjectNode();
            sortedFields.forEach(result::set);
            return result;
        }
        if (node.isArray()) {
            ArrayNode array = objectMapper.createArrayNode();
            node.forEach(n -> array.add(sortNode(n)));
            return array;
        }
        return node;
    }
}
