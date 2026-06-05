package dev.nelit.api.dto.request.checkout;

import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.enums.PaymentType;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
    @NotNull PaymentGateway gateway,
    @NotNull CheckoutDetails details
) {
}
