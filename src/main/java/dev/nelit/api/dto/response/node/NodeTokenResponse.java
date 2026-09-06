package dev.nelit.api.dto.response.node;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NodeTokenResponse(
    String token,
    @JsonProperty("expires_in_seconds") long expiresInSeconds
) {}