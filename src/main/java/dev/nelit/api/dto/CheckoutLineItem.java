package dev.nelit.api.dto;

import java.math.BigDecimal;

public record CheckoutLineItem(
    String description,
    BigDecimal unitAmount,
    String currency,
    long quantity) {
}
