package dev.nelit.api.domain.exception.user;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends DomainException {
    public InsufficientBalanceException() {
        super("INSUFFICIENT_BALANCE", "Insufficient balance", HttpStatus.BAD_REQUEST);
    }
}
