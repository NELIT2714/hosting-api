package dev.nelit.api.dto.request.checkout;

import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.enums.PaymentType;

public record CheckoutRequest(
    PaymentGateway gateway,
    PaymentType type,
    CheckoutDetails details
) {
}
