package dev.nelit.api.domain.exception.admin;

import dev.nelit.api.domain.exception.DomainException;

public class UserIsNotAdminException extends DomainException {
    public UserIsNotAdminException() {
        super("USER_IS_NOT_ADMIN", "User is not an admin");
    }
}
