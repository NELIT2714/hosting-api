package dev.nelit.api.services;

public interface JwtService {
    String generate(Long idUser);
    Long extractUserId(String token);
    boolean isValid(String token);
}
