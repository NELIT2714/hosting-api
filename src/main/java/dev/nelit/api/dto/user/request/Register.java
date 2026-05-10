package dev.nelit.api.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;

public record Register(
    @Email(message = "Invalid email format") String email,
    String password,
    @JsonProperty("repeated_password") String repeatedPassword
) {
    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordsMatching() {
        return password != null && password.equals(repeatedPassword);
    }
}