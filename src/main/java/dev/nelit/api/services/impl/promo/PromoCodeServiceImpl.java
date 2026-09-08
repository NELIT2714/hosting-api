package dev.nelit.api.services.impl.promo;

import dev.nelit.api.domain.entity.promo.PromoCode;
import dev.nelit.api.domain.exception.promo.PromoCodeAlreadyExistsException;
import dev.nelit.api.domain.exception.promo.PromoCodeNotFoundException;
import dev.nelit.api.dto.request.promo.CreatePromoCode;
import dev.nelit.api.dto.response.PromoCodeResponse;
import dev.nelit.api.repository.promo.PromoCodeRepository;
import dev.nelit.api.services.promo.PromoCodeService;
import dev.nelit.api.services.promo.PromoCodeUseService;
import dev.nelit.api.util.PromoCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeUseService promoCodeUseService;
    private final TransactionalOperator to;

    @Override
    public Mono<Integer> peekDiscount(String code) {
        return promoCodeRepository.findActiveByCode(code)
            .switchIfEmpty(Mono.error(new PromoCodeNotFoundException()))
            .map(PromoCode::getDiscount);
    }

    @Override
    public Mono<PromoCodeResponse> applyToPayment(String code, Long userId, Long idPayment) {
        return promoCodeRepository.findLockedByCode(code)
            .switchIfEmpty(Mono.error(new PromoCodeNotFoundException()))
            .flatMap(promo -> promoCodeUseService.reserve(promo, userId, idPayment))
            .as(to::transactional);
    }

    @Override
    public Mono<PromoCodeResponse> create(CreatePromoCode promoCodeDTO) {
        String code = promoCodeDTO.promoCode() != null ? promoCodeDTO.promoCode() : PromoCodeGenerator.generate();

        PromoCode newPromoCode = PromoCode.builder()
            .code(code)
            .discount(promoCodeDTO.discount())
            .amountOfUses(promoCodeDTO.amountOfUses())
            .expiresAt(promoCodeDTO.expiresAt())
            .build();

        return promoCodeRepository.save(newPromoCode)
            .onErrorMap(DuplicateKeyException.class, _ -> new PromoCodeAlreadyExistsException())
            .map(PromoCodeResponse::fromEntity);
    }

    @Override
    public Mono<Void> delete(long promoId) {
        return promoCodeRepository.deleteById(promoId);
    }
}