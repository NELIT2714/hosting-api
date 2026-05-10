package dev.nelit.api.domain.exception.user;

import dev.nelit.api.domain.exception.DomainException;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException() {
        super("USER_NOT_FOUND", "User not found");
    }
}
