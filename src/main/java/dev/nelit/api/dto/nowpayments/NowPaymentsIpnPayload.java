package dev.nelit.api.dto.nowpayments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NowPaymentsIpnPayload(
    @JsonProperty("payment_id") Long paymentId,
    @JsonProperty("payment_status") String paymentStatus,
    @JsonProperty("order_id") String orderId,
    @JsonProperty("price_amount") BigDecimal priceAmount,
    @JsonProperty("price_currency") String priceCurrency,
    @JsonProperty("actually_paid") BigDecimal actuallyPaid
) {}
