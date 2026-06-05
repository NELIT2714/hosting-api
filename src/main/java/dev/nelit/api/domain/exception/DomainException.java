package dev.nelit.api.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class DomainException extends RuntimeException {

    private final String code;
    private HttpStatus status;

    protected DomainException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    protected DomainException(String code, String message) {
        this(code, message, HttpStatus.BAD_REQUEST);
    }
}
