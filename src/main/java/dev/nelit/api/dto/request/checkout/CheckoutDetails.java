package dev.nelit.api.dto.request.checkout;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.nelit.api.domain.entity.vps.VpsRenewalOrder;
import dev.nelit.api.enums.PaymentType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = VpsCheckoutDetails.class, name = "VPS_PURCHASE"),
    @JsonSubTypes.Type(value = VpsRenewalDetails.class, name = "VPS_RENEWAL"),
})
public sealed interface CheckoutDetails permits VpsRenewalDetails, VpsCheckoutDetails {
    PaymentType type();
}
