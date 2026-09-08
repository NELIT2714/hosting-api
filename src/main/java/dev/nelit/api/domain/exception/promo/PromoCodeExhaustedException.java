package dev.nelit.api.domain.exception.promo;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PromoCodeExhaustedException extends DomainException {
    public PromoCodeExhaustedException() {
        super("PROMO_CODE_EXHAUSTED", "Promo code usage limit has been reached", HttpStatus.CONFLICT);
    }
}
