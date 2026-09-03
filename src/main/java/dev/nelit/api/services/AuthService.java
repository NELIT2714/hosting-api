package dev.nelit.api.services;

import dev.nelit.api.dto.request.user.Login;
import dev.nelit.api.dto.response.AuthTokens;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthTokens> login(Login loginDTO);
    Mono<String> refresh(String rawRefreshToken);
    Mono<Void> logout(String rawRefreshToken);
}
