package dev.nelit.api.domain.exception.vm;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VmAlreadyActiveException extends DomainException {
    public VmAlreadyActiveException() {
        super("VM_ALREADY_ACTIVATED", "Vm already activated", HttpStatus.CONFLICT);
    }
}
