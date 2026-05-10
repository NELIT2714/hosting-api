package dev.nelit.api.domain.exception.user;

import dev.nelit.api.domain.exception.DomainException;

public class CurrentPasswordIncorrectException extends DomainException {

    public CurrentPasswordIncorrectException() {
        super("CURRENT_PASSWORD_INCORRECT", "Current password is incorrect");
    }
}
