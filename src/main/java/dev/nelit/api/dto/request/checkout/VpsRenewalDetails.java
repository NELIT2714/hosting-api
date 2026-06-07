package dev.nelit.api.dto.request.checkout;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.enums.PaymentType;
import jakarta.validation.constraints.NotNull;

public record VpsRenewalDetails(
    @JsonProperty("id_vm") @NotNull Long idVm
) implements CheckoutDetails {

    @Override
    public PaymentType type() {
        return PaymentType.VPS_RENEWAL;
    }
}
