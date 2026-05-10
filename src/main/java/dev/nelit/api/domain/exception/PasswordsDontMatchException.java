package dev.nelit.api.domain.exception;

public class PasswordsDontMatchException extends DomainException {

    public PasswordsDontMatchException() {
        super("PASSWORDS_DO_NOT_MATCH", "Password confirmation does not match");
    }
}
