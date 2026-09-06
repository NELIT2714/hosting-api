package dev.nelit.api.services.impl;

import dev.nelit.api.services.EnrollmentTokenService;
import dev.nelit.api.util.TokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class EnrollmentTokenServiceImpl implements EnrollmentTokenService {

    private static final String KEY_PREFIX = "enrollment-token:";

    private final ReactiveStringRedisTemplate redis;
    private final SecureRandom random = new SecureRandom();


    @Override
    public Mono<String> issueToken(Long nodeId, Duration ttl) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String rawToken = Base64.getEncoder().withoutPadding().encodeToString(bytes);
        return redis.opsForValue()
            .set(KEY_PREFIX + TokenHasher.hash(rawToken), String.valueOf(nodeId), ttl)
            .thenReturn(rawToken);
    }

    public Mono<Long> consumeToken(String rawToken) {
        return redis.opsForValue()
            .getAndDelete(KEY_PREFIX + TokenHasher.hash(rawToken))
            .map(Long::valueOf);
    }
}
