package dev.nelit.api.services;

public interface JwtService {
    String generate(Long idUser, String role);
    Long extractUserId(String token);
    String extractRole(String token);
    boolean isValid(String token);
}