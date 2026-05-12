package dev.nelit.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record AdminResponse(
    @JsonProperty("id_admin") Long idAdmin,
    @JsonProperty("id_user") Long idUser,
    @JsonProperty("permissions") List<String> permissions,
    @JsonProperty("created_at") Instant createdAt
) {}