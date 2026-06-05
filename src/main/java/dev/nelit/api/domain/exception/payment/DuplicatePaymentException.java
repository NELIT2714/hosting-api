package dev.nelit.api.domain.exception.payment;

import com.google.api.Http;
import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class DuplicatePaymentException extends DomainException {
    public DuplicatePaymentException() {
        super("DUPLICATE_PAYMENT", "Payment already exists", HttpStatus.CONFLICT);
    }
}
