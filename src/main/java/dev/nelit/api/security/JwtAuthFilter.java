package dev.nelit.api.security;

import dev.nelit.api.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpCookie tokenCookie = exchange.getRequest().getCookies().getFirst("token");
        if (tokenCookie == null) return chain.filter(exchange);

        String token = tokenCookie.getValue();

        if (!jwtService.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Long idUser = jwtService.extractUserId(token);
        return chain.filter(exchange).contextWrite(ctx -> ctx.put("id_user", idUser));
    }
}
