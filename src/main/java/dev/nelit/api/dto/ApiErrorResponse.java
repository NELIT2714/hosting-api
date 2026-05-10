package dev.nelit.api.dto;

public record ApiErrorResponse(
    String code,
    String message,
    int status
) {}
