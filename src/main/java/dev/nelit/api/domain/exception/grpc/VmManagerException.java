package dev.nelit.api.domain.exception.grpc;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VmManagerException extends DomainException {
    public VmManagerException(String details) {
        super("VM_MANAGER_ERROR", "An error occurred while communicating with the virtual machine manager" + (details != null ? ": " + details : ""), HttpStatus.BAD_GATEWAY);
    }
}
