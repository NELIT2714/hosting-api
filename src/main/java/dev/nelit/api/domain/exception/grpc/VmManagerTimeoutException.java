package dev.nelit.api.domain.exception.grpc;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VmManagerTimeoutException extends DomainException {
    public VmManagerTimeoutException() {
        super("VM_MANAGER_TIMEOUT", "Virtual machine manager timed out", HttpStatus.GATEWAY_TIMEOUT);
    }
}
