package dev.nelit.api.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record UserResponse(
    @JsonProperty("id_user") Long idUser,
    String email,
    @JsonProperty("created_at") Instant createdAt
) {}
