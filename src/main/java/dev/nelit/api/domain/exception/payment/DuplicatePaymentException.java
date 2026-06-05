package dev.nelit.api.domain.exception.payment;

import dev.nelit.api.domain.exception.DomainException;

public class DuplicatePaymentException extends DomainException {
    public DuplicatePaymentException() {
        super("DUPLICATE_PAYMENT", "Payment already exists");
    }
}
