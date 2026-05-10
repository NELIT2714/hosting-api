package dev.nelit.api.exception.domain;

public class UserNotFoundException extends DomainException{

    public UserNotFoundException() {
        super("USER_NOT_FOUND", "User not found");
    }
}
