package dev.nelit.api.domain.exception.grpc;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VmManagerUnavailableException extends DomainException {
    public VmManagerUnavailableException() {
        super("VM_MANAGER_UNAVAILABLE", "Error accessing the virtual machine management service", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
