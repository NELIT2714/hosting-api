package dev.nelit.api.domain.exception.user;

import dev.nelit.api.domain.exception.DomainException;

public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException() {
        super("INVALID_PASSWORD", "Invalid password");
    }
}
