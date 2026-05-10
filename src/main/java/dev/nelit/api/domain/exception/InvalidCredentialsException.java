package dev.nelit.api.domain.exception;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid credentials");
    }
}
