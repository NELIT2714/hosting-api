package dev.nelit.api.dto.request.checkout;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.nelit.api.enums.PaymentType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = VpsCheckoutDetails.class, name = "VPS_PURCHASE"),
})
public sealed interface CheckoutDetails permits VpsCheckoutDetails {
    PaymentType type();
}
