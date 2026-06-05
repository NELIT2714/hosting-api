package dev.nelit.api.domain.exception.user;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException() {
        super("EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.CONFLICT);
    }
}
