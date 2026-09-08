package dev.nelit.api.domain.exception.promo;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PromoCodeAlreadyUsedByUserException extends DomainException {
    public PromoCodeAlreadyUsedByUserException() {
        super("PROMO_CODE_ALREADY_USED_BY_USER", "Promo code already used by user", HttpStatus.CONFLICT);
    }
}