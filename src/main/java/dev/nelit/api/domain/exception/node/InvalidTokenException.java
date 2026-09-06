package dev.nelit.api.domain.exception.node;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException() {
        super("INVALID_TOKEN", "Invalid, expired, or already used token", HttpStatus.UNAUTHORIZED);
    }
}
