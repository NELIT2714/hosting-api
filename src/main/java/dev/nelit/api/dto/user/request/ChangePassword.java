package dev.nelit.api.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChangePassword(
    @JsonProperty("current_password") String currentPassword,
    @JsonProperty("new_password") String newPassword)
{}
