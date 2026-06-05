package dev.nelit.api.domain.exception.user;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException() {
        super("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND);
    }
}
