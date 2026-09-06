package dev.nelit.api.domain.exception.promo;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PromoCodeAlreadyExistsException extends DomainException {

    public PromoCodeAlreadyExistsException() {
        super("PROMO_CODE_ALREADY_EXISTS", "Promo code already exists", HttpStatus.CONFLICT);
    }
}
