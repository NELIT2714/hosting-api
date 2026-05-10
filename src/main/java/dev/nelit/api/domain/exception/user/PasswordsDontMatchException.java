package dev.nelit.api.domain.exception.user;

import dev.nelit.api.domain.exception.DomainException;

public class PasswordsDontMatchException extends DomainException {

    public PasswordsDontMatchException() {
        super("PASSWORDS_DO_NOT_MATCH", "Password confirmation does not match");
    }
}
