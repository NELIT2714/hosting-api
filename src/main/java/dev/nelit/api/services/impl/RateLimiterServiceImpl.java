package dev.nelit.api.services.impl;

import dev.nelit.api.domain.RateLimitRule;
import dev.nelit.api.services.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisScript<List> rateLimiterScript;

    @Override
    public Mono<RateLimitResult> checkLimit(String key, RateLimitRule rule) {
        long now = Instant.now().getEpochSecond();

        List<String> keys = List.of("rl:" + key);
        List<String> args = List.of(
            String.valueOf(rule.capacity()),
            String.valueOf(rule.refillPerSecond()),
            String.valueOf(now),
            "1"
        );

        return redisTemplate.execute(rateLimiterScript, keys, args)
            .single()
            .map(result -> {
                @SuppressWarnings("unchecked")
                List<Long> r = (List<Long>) result;
                return new RateLimitResult(r.get(0) == 1L, r.get(1), r.get(2));
            });
    }
}
