package dev.nelit.api.dto.nowpayments;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record NowPaymentsInvoiceRequest(
    @JsonProperty("price_amount") BigDecimal priceAmount,
    @JsonProperty("price_currency") String priceCurrency,
    @JsonProperty("order_id") String orderId,
    @JsonProperty("order_description") String orderDescription,
    @JsonProperty("ipn_callback_url") String ipnCallbackUrl,
    @JsonProperty("success_url") String successUrl,
    @JsonProperty("cancel_url") String cancelUrl
) {}
