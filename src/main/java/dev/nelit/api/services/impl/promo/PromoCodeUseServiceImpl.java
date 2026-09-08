package dev.nelit.api.services.impl.promo;

import dev.nelit.api.domain.entity.promo.PromoCode;
import dev.nelit.api.domain.exception.promo.PromoCodeAlreadyUsedByUserException;
import dev.nelit.api.domain.exception.promo.PromoCodeExhaustedException;
import dev.nelit.api.domain.exception.promo.PromoCodeNotFoundException;
import dev.nelit.api.dto.response.PromoCodeResponse;
import dev.nelit.api.repository.promo.PromoCodeRepository;
import dev.nelit.api.repository.promo.PromoCodeUseRepository;
import dev.nelit.api.services.promo.PromoCodeUseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromoCodeUseServiceImpl implements PromoCodeUseService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeUseRepository promoCodeUseRepository;
    private final TransactionalOperator to;


    @Override
    public Mono<PromoCodeResponse> reserve(PromoCode promo, Long userId, Long idPayment) {
        return promoCodeRepository.existsActiveUseByUser(promo.getIdPromo(), userId)
            .flatMap(alreadyUsed -> {
                if (alreadyUsed) {
                    return Mono.error(new PromoCodeAlreadyUsedByUserException());
                }
                return checkLimitAndReserve(promo, userId, idPayment);
            });
    }

    private Mono<PromoCodeResponse> checkLimitAndReserve(PromoCode promo, Long userId, Long idPayment) {
        if (promo.getAmountOfUses() == null) {
            return promoCodeUseRepository.reserve(userId, promo.getIdPromo(), idPayment)
                .thenReturn(PromoCodeResponse.fromEntity(promo));
        }
        return promoCodeRepository.countActiveUses(promo.getIdPromo())
            .flatMap(count -> {
                if (count >= promo.getAmountOfUses()) {
                    return Mono.error(new PromoCodeExhaustedException());
                }
                return promoCodeUseRepository.reserve(userId, promo.getIdPromo(), idPayment)
                    .thenReturn(PromoCodeResponse.fromEntity(promo));
            });
    }

    @Override
    public Mono<Void> confirmByPayment(Long idPayment) {
        return promoCodeUseRepository.confirmByPayment(idPayment)
            .as(to::transactional)
            .flatMap(updated -> {
                if (updated == 0) {
                    log.warn("No RESERVED promo_codes_uses found for idPayment={} on confirm", idPayment);
                }
                return Mono.empty();
            })
            .then();
    }

    @Override
    public Mono<Void> cancelByPayment(Long idPayment) {
        return promoCodeUseRepository.cancelByPayment(idPayment)
            .as(to::transactional)
            .flatMap(updated -> {
                if (updated == 0) {
                    log.warn("No RESERVED promo_codes_uses found for idPayment={} on cancel", idPayment);
                }
                return Mono.empty();
            })
            .then();
    }

    @Override
    public Mono<Void> releaseExpired() {
        return promoCodeUseRepository.releaseExpired().then();
    }
}