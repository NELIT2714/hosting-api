package dev.nelit.api.exception.domain;

public class CurrentPasswordIncorrectException extends DomainException {

    public CurrentPasswordIncorrectException() {
        super("CURRENT_PASSWORD_INCORRECT", "Current password is incorrect");
    }
}
