package dev.nelit.api.domain.exception.node;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class FailedToSignCsrException extends DomainException {
    public FailedToSignCsrException(String errorMessage) {
        super("FAILED_TO_SIGN_CSR", "Failed to sign CSR: " + errorMessage, HttpStatus.BAD_REQUEST);
    }
}
