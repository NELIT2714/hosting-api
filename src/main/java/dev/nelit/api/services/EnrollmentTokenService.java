package dev.nelit.api.services;

import reactor.core.publisher.Mono;

import java.time.Duration;

public interface EnrollmentTokenService {
    Mono<String> issueToken(Long nodeId, Duration ttl);
    Mono<Long> consumeToken(String rawToken);
}
