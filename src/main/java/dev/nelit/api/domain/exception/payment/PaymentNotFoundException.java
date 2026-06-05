package dev.nelit.api.domain.exception.payment;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends DomainException {
    public PaymentNotFoundException() {
        super("PAYMENT_NOT_FOUND", "Payment not found", HttpStatus.NOT_FOUND);
    }
}
