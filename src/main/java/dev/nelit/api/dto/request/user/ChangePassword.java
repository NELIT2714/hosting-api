package dev.nelit.api.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record ChangePassword(
    @JsonProperty("current_password") @NotBlank String currentPassword,
    @JsonProperty("new_password") @NotBlank String newPassword,
    @JsonProperty("repeat_new_password") @NotBlank String repeatNewPassword)
{}
