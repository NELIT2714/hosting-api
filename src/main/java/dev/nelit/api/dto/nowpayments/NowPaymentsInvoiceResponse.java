package dev.nelit.api.dto.nowpayments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NowPaymentsInvoiceResponse(
    @JsonProperty("id") Long id,
    @JsonProperty("order_id") String orderId,
    @JsonProperty("price_amount") BigDecimal priceAmount,
    @JsonProperty("price_currency") String priceCurrency,
    @JsonProperty("invoice_url") String invoiceUrl
) {}
