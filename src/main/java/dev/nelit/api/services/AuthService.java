package dev.nelit.api.services;

import dev.nelit.api.dto.user.request.Login;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<String> login(Login loginDTO);
}
