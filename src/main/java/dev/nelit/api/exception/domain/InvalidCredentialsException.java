package dev.nelit.api.exception.domain;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid credentials");
    }
}
