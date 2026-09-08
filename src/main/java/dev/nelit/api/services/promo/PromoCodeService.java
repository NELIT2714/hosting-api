package dev.nelit.api.services.promo;

import dev.nelit.api.domain.entity.promo.PromoCode;
import dev.nelit.api.dto.request.promo.CreatePromoCode;
import dev.nelit.api.dto.response.PromoCodeResponse;
import reactor.core.publisher.Mono;

public interface PromoCodeService {
    Mono<Integer> peekDiscount(String code);
    Mono<PromoCodeResponse> applyToPayment(String code, Long userId, Long idPayment);
    Mono<PromoCodeResponse> create(CreatePromoCode dto);
    Mono<Void> delete(long promoId);
}
