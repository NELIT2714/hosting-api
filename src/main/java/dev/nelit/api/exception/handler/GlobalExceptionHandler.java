package dev.nelit.api.exception.handler;

import dev.nelit.api.dto.ApiErrorResponse;
import dev.nelit.api.exception.domain.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> handleDomain(DomainException ex) {
        HttpStatus status = mapStatus(ex);

        return ResponseEntity
            .status(status)
            .body(new ApiErrorResponse(
                ex.getCode(),
                ex.getMessage(),
                status.value()
            ));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(WebExchangeBindException ex) {
        FieldError fieldError = ex.getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Validation failed";

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiErrorResponse(
                "VALIDATION_ERROR",
                message,
                HttpStatus.BAD_REQUEST.value()
            ));
    }

    private HttpStatus mapStatus(DomainException ex) {
        return switch (ex.getCode()) {
            case "EMAIL_ALREADY_EXISTS" -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
