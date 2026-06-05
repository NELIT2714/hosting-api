package dev.nelit.api.domain.exception.admin;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class AdminNotFoundException extends DomainException {
    public AdminNotFoundException() {
        super("ADMIN_NOT_FOUND", "Admin not found", HttpStatus.NOT_FOUND);
    }
}
