package dev.nelit.api.dto.user.request;

import jakarta.validation.constraints.Email;

public record Login(
    @Email String email,
    String password)
{}
