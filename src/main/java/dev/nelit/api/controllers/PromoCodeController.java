package dev.nelit.api.controllers;

import dev.nelit.api.domain.entity.promo.PromoCode;
import dev.nelit.api.dto.request.promo.CreatePromoCode;
import dev.nelit.api.services.PromoCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/promo-codes")
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PromoCode> create(@Valid @RequestBody CreatePromoCode promoCodeDTO) {
        return promoCodeService.create(promoCodeDTO.promoCode(), promoCodeDTO.discount(), promoCodeDTO.amountOfUses(), promoCodeDTO.expiresAt());
    }

}
