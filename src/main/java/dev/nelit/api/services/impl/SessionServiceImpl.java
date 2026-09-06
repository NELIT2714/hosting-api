package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.Session;
import dev.nelit.api.domain.exception.user.session.InvalidSessionException;
import dev.nelit.api.repository.SessionRepository;
import dev.nelit.api.services.SessionService;
import dev.nelit.api.util.TokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private static final Duration REFRESH_TTL = Duration.ofDays(30);

    private final SessionRepository sessionRepository;

    @Override
    public Mono<String> create(Long idUser) {
        String rawToken = generateRawToken();

        Session session = Session.builder()
            .idUser(idUser)
            .refreshTokenHash(TokenHasher.hash(rawToken))
            .expiresAt(Instant.now().plus(REFRESH_TTL))
            .build();

        return sessionRepository.save(session).thenReturn(rawToken);
    }


    @Override
    public Mono<Session> findActiveByRawToken(String rawToken) {
        return sessionRepository.findByRefreshTokenHash(TokenHasher.hash(rawToken))
            .switchIfEmpty(Mono.error(new InvalidSessionException()))
            .flatMap(session -> {
                if (session.getRevokedAt() != null) {
                    return Mono.error(new InvalidSessionException());
                }
                if (session.getExpiresAt().isBefore(Instant.now())) {
                    return Mono.error(new InvalidSessionException());
                }
                return Mono.just(session);
            });
    }

    @Override
    public Mono<Void> revoke(Long idSession, String reason) {
        return sessionRepository.findById(idSession)
            .switchIfEmpty(Mono.error(new InvalidSessionException()))
            .flatMap(session -> {
                session.setRevokedAt(Instant.now());
                session.setRevokeReason(reason);
                return sessionRepository.save(session);
            })
            .then();
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
