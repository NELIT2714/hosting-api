package dev.nelit.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.enums.PaymentType;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
    @JsonProperty("id_payment") Long idPayment,
    @JsonProperty("id_user") Long idUser,
    @JsonProperty("gateway") PaymentGateway gateway,
    @JsonProperty("gateway_payment_id") String gatewayPaymentId,
    @JsonProperty("amount") BigDecimal amount,
    @JsonProperty("currency") String currency,
    @JsonProperty("status") PaymentStatus status,
    @JsonProperty("type") PaymentType type,
    @JsonProperty("created_at") Instant createdAt
) {}
