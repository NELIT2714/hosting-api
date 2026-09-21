package dev.nelit.api.dto.response;

public record RateLimitErrorResponse(
    String error,
    String message,
    long retryAfterSeconds
) {}
