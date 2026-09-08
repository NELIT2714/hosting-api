package dev.nelit.api.domain.exception.promo;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PromoCodeNotFoundException extends DomainException {
    public PromoCodeNotFoundException() {
        super("PROMO_CODE_NOT_FOUND", "Promo code not found", HttpStatus.NOT_FOUND);
    }
}
