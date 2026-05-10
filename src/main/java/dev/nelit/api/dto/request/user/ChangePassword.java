package dev.nelit.api.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChangePassword(
    @JsonProperty("current_password") String currentPassword,
    @JsonProperty("new_password") String newPassword,
    @JsonProperty("repeated_new_password") String repeatedNewPassword)
{}
