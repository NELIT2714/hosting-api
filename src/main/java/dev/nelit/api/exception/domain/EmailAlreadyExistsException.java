package dev.nelit.api.exception.domain;

public class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException() {
        super("EMAIL_ALREADY_EXISTS", "Email already exists");
    }
}
