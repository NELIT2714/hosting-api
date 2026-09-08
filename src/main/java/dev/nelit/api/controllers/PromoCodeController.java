package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.promo.CreatePromoCode;
import dev.nelit.api.dto.response.PromoCodeResponse;
import dev.nelit.api.services.promo.PromoCodeService;
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
    public Mono<PromoCodeResponse> create(@Valid @RequestBody CreatePromoCode promoCodeDTO) {
        return promoCodeService.create(promoCodeDTO);
    }

    @DeleteMapping("/{promo_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable("promo_id") Long promoId) {
        return promoCodeService.delete(promoId);
    }
}
