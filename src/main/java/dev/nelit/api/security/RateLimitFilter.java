package dev.nelit.api.security;

import dev.nelit.api.domain.RateLimitRule;
import dev.nelit.api.dto.response.RateLimitErrorResponse;
import dev.nelit.api.services.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RateLimitFilter implements WebFilter {

    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        return ReactiveSecurityContextHolder.getContext()
            .mapNotNull(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(auth -> "user:" + auth.getName())
            .switchIfEmpty(Mono.just("ip:" + resolveClientIp(request)))
            .flatMap(key -> {
                RateLimitRule rule = key.startsWith("user:") ? RateLimitRule.USER : RateLimitRule.ANONYMOUS;
                return applyLimit(exchange, chain, key, rule);
            });
    }

    private Mono<Void> applyLimit(ServerWebExchange exchange, WebFilterChain chain,
                                  String key, RateLimitRule rule) {
        return rateLimiterService.checkLimit(key, rule)
            .flatMap(result -> {
                exchange.getResponse().getHeaders()
                    .add("X-Rate-Limit-Remaining", String.valueOf(result.remainingTokens()));

                if (result.allowed()) {
                    return chain.filter(exchange);
                }

                return writeRateLimitExceeded(exchange, result.waitSeconds());
            });
    }

    private String resolveClientIp(ServerHttpRequest request) {
        String xff = request.getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return request.getRemoteAddress() != null
            ? request.getRemoteAddress().getAddress().getHostAddress()
            : "unknown";
    }

    private Mono<Void> writeRateLimitExceeded(ServerWebExchange exchange, long waitSeconds) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().add("Retry-After", String.valueOf(waitSeconds));

        RateLimitErrorResponse error = new RateLimitErrorResponse("rate_limit_exceeded", "Too many requests, retry after " + waitSeconds + " seconds", waitSeconds);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(error);
        } catch (Exception e) {
            bytes = "{\"error\":\"rate_limit_exceeded\"}".getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
