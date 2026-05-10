package dev.nelit.api.domain.exception;

public class CurrentPasswordIncorrectException extends DomainException {

    public CurrentPasswordIncorrectException() {
        super("CURRENT_PASSWORD_INCORRECT", "Current password is incorrect");
    }
}
