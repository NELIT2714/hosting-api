package dev.nelit.api.dto.request.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.enums.AdminPermission;

import java.util.List;

public record CreateAdmin(
    @JsonProperty("id_user") Long idUser,
    @JsonProperty("permissions") List<AdminPermission> permissions
) {}