package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.Session;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SessionRepository extends ReactiveCrudRepository<Session, Long> {
    Mono<Session> findByRefreshTokenHash(String refreshTokenHash);
}
