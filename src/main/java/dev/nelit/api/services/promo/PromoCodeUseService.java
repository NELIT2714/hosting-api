package dev.nelit.api.services.promo;

import dev.nelit.api.domain.entity.promo.PromoCode;
import dev.nelit.api.dto.response.PromoCodeResponse;
import reactor.core.publisher.Mono;

public interface PromoCodeUseService {
    Mono<PromoCodeResponse> reserve(PromoCode promo, Long userId, Long idPayment);
    Mono<Void> confirmByPayment(Long idPayment);
    Mono<Void> cancelByPayment(Long idPayment);
    Mono<Void> releaseExpired();
}
