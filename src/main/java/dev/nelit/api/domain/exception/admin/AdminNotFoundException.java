package dev.nelit.api.domain.exception.admin;

import dev.nelit.api.domain.exception.DomainException;

public class AdminNotFoundException extends DomainException {
    public AdminNotFoundException() {
        super("ADMIN_NOT_FOUND", "Admin not found");
    }
}
