package dev.nelit.api.services;

import dev.nelit.api.domain.entity.promo.PromoCode;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface PromoCodeService {
    Mono<PromoCode> create(String rawPromoCode, Integer discount, Integer amountOfUses, Instant expiresAt);
}
