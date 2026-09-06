package dev.nelit.api.domain.exception.payment;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class CheckoutTypeNotSupportedException extends DomainException {
    public CheckoutTypeNotSupportedException() {
        super("CHECKOUT_TYPE_NOT_SUPPORTED", "Checkout details type is not supported", HttpStatus.BAD_REQUEST);
    }
}
