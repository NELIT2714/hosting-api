package dev.nelit.api.services;

import dev.nelit.api.domain.RateLimitRule;
import reactor.core.publisher.Mono;

public interface RateLimiterService {
    record RateLimitResult(boolean allowed, long remainingTokens, long waitSeconds) {}
    Mono<RateLimitResult> checkLimit(String key, RateLimitRule rule);
}
