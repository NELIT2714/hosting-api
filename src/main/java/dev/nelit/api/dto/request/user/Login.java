package dev.nelit.api.dto.request.user;

import jakarta.validation.constraints.Email;

public record Login(
    @Email String email,
    String password)
{}
