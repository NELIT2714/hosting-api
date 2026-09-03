package dev.nelit.api.services;

import dev.nelit.api.domain.entity.Session;
import reactor.core.publisher.Mono;

public interface SessionService {
    Mono<String> create(Long idUser);
    Mono<Session> findActiveByRawToken(String rawToken);
    Mono<Void> revoke(Long idSession, String reason);
}
