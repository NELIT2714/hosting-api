package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.promo.PromoCode;
import dev.nelit.api.domain.exception.promo.PromoCodeAlreadyExistsException;
import dev.nelit.api.repository.promo.PromoCodeRepository;
import dev.nelit.api.services.PromoCodeService;
import dev.nelit.api.util.PromoCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    @Override
    public Mono<PromoCode> create(String rawPromoCode, Integer discount, Integer amountOfUses, Instant expiresAt) {
        String code = rawPromoCode != null ? rawPromoCode : PromoCodeGenerator.generate();

        PromoCode newPromoCode = PromoCode.builder()
            .code(code)
            .discount(discount)
            .amountOfUses(amountOfUses)
            .expiresAt(expiresAt)
            .build();

        return promoCodeRepository.save(newPromoCode)
            .onErrorMap(DuplicateKeyException.class, _ -> new PromoCodeAlreadyExistsException());
    }
}
