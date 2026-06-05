package dev.nelit.api.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record Register(
    @Email(message = "Invalid email format") @NotBlank String email,
    @NotBlank String password,
    @JsonProperty("repeated_password") @NotBlank String repeatedPassword
) {
    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordsMatching() {
        return password != null && password.equals(repeatedPassword);
    }
}