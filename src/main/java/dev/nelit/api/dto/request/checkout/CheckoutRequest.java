package dev.nelit.api.dto.request.checkout;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import javax.annotation.Nullable;

public record CheckoutRequest(
    @NotNull PaymentGateway gateway,
    @NotNull CheckoutDetails details,
    @Nullable @JsonProperty("promo_code") String promoCode
) {
}
