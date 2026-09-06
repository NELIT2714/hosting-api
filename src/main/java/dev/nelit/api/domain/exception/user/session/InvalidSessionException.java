package dev.nelit.api.domain.exception.user.session;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidSessionException extends DomainException {
    public InvalidSessionException() {
        super("INVALID_SESSION", "Session is invalid, expired or revoked", HttpStatus.UNAUTHORIZED);
    }
}
