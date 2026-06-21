package dev.nelit.api.domain.exception.vm;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VmNotActiveException extends DomainException {
    public VmNotActiveException() {
        super("VM_NOT_ACTIVE", "Vm not active", HttpStatus.CONFLICT);
    }
}
