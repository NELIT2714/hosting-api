package dev.nelit.api.dto.request.promo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import javax.annotation.Nullable;
import java.time.Instant;

public record UpdatePromoCode(
    @JsonProperty("promo_code")
    String promoCode,

    @Min(1) @Max(99)
    @JsonProperty("discount")
    Integer discount,

    @Positive
    @JsonProperty("amount_of_uses")
    Integer amountOfUses,

    @Future
    @JsonProperty("expires_at")
    Instant expiresAt
) {
}
