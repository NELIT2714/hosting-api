package dev.nelit.api.domain.exception.handler;

import dev.nelit.api.domain.exception.DomainException;
import dev.nelit.api.dto.ApiErrorResponse;
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
            case "EMAIL_ALREADY_EXISTS", "PLAN_NAME_ALREADY_TAKEN", "NODE_NAME_ALREADY_TAKEN" -> HttpStatus.CONFLICT;
            case "USER_NOT_FOUND", "PLAN_NOT_FOUND", "NODE_NOT_FOUND", "OS_IMAGE_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
