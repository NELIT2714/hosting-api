package dev.nelit.api.dto.request.promo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import javax.annotation.Nullable;
import java.time.Instant;

public record CreatePromoCode(
    @Nullable
    @NotBlank
    @JsonProperty("promo_code")
    String promoCode,

    @NotNull
    @Min(1) @Max(100)
    @JsonProperty("discount")
    Integer discount,

    @Nullable
    @Positive
    @JsonProperty("amount_of_uses")
    Integer amountOfUses,

    @Nullable
    @Future
    @JsonProperty("expires_at")
    Instant expiresAt
) {
}
