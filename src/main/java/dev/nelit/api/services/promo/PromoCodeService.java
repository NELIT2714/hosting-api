package dev.nelit.api.services.promo;

import dev.nelit.api.dto.request.promo.CreatePromoCode;
import dev.nelit.api.dto.request.promo.UpdatePromoCode;
import dev.nelit.api.dto.response.PromoCodeResponse;
import reactor.core.publisher.Mono;

public interface PromoCodeService {
    Mono<Integer> peekDiscount(String code);
    Mono<PromoCodeResponse> applyToPayment(String code, long userId, long idPayment);

    Mono<PromoCodeResponse> create(CreatePromoCode dto);
    Mono<PromoCodeResponse> update(long promoId, UpdatePromoCode promoCodeDTO);
    Mono<Void> delete(long promoId);
}
