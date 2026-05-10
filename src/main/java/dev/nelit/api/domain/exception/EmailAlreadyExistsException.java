package dev.nelit.api.domain.exception;

public class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException() {
        super("EMAIL_ALREADY_EXISTS", "Email already exists");
    }
}
