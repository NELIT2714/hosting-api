package dev.nelit.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.enums.AdminPermission;

import java.time.Instant;
import java.util.List;

public record AdminResponse(
    @JsonProperty("id_admin") Long idAdmin,
    @JsonProperty("id_user") Long idUser,
    @JsonProperty("permissions") List<AdminPermission> permissions,
    @JsonProperty("created_at") Instant createdAt
) {}