package dev.nelit.api.domain.exception.vm;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VMAlreadyActiveException extends DomainException {
    public VMAlreadyActiveException() {
        super("VM_ALREADY_ACTIVATED", "VM already activated", HttpStatus.CONFLICT);
    }
}
