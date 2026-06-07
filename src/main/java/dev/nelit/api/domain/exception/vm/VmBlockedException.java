package dev.nelit.api.domain.exception.vm;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VmBlockedException extends DomainException {
    public VmBlockedException() {
        super("VM_BLOCKED", "VM blocked", HttpStatus.LOCKED);
    }
}
