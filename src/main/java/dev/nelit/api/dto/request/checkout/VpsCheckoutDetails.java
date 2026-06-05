package dev.nelit.api.dto.request.checkout;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.enums.PaymentType;
import jakarta.validation.constraints.NotNull;

public record VpsCheckoutDetails(
    @JsonProperty("id_plan") @NotNull Long idPlan,
    @JsonProperty("id_os_image") @NotNull Long idOsImage
) implements CheckoutDetails {

    @Override
    public PaymentType type() {
        return PaymentType.VPS_PURCHASE;
    }
}
