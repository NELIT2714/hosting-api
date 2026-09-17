package dev.nelit.api.dto.request.checkout;

import dev.nelit.api.enums.PaymentType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BalanceTopUpDetails(
    @NotNull
    BigDecimal amount
) implements CheckoutDetails {

    @Override
    public PaymentType type() {
        return PaymentType.BALANCE_TOPUP;
    }
}
