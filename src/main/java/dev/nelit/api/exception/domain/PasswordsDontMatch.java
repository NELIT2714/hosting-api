package dev.nelit.api.exception.domain;

public class PasswordsDontMatch extends DomainException {

    public PasswordsDontMatch() {
        super("PASSWORDS_DO_NOT_MATCH", "Password confirmation does not match");
    }
}
