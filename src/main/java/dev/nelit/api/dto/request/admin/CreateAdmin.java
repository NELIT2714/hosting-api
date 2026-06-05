package dev.nelit.api.dto.request.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.enums.AdminPermission;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateAdmin(
    @JsonProperty("id_user") @NotNull Long idUser,
    @JsonProperty("permissions") @NotNull List<AdminPermission> permissions
) {}