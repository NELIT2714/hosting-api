package dev.nelit.api.dto.response;

import dev.nelit.api.domain.entity.promo.PromoCode;

import java.time.Instant;

public record PromoCodeResponse(
    String code,
    int discount,
    Integer amountOfUses,
    Instant expiresAt
) {
    public static PromoCodeResponse fromEntity(PromoCode promoCode) {
        return new PromoCodeResponse(
            promoCode.getCode(),
            promoCode.getDiscount(),
            promoCode.getAmountOfUses(),
            promoCode.getExpiresAt()
        );
    }
}
