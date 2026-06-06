package dev.nelit.api.domain.exception.vm;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VmNotFoundException extends DomainException {
    public VmNotFoundException() {
        super("VM_NOT_FOUND", "Vm not found", HttpStatus.NOT_FOUND);
    }
}
