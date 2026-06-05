package dev.nelit.api.dto.request.checkout;

import dev.nelit.api.enums.PaymentType;

public record VpsCheckoutDetails(
    Long idPlan,
    Long idOsImage
) implements CheckoutDetails {

    @Override
    public PaymentType type() {
        return PaymentType.VPS_PURCHASE;
    }
}
