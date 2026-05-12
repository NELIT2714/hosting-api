package dev.nelit.api.security;

import dev.nelit.api.services.AdminService;
import dev.nelit.api.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final JwtService jwtService;
    private final AdminService adminService;

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

        return adminService.getByUserId(idUser)
            .switchIfEmpty(Mono.defer(() -> {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete().then(Mono.empty());
            }))
            .flatMap(admin -> adminService.getPermissions(admin.getIdAdmin())
                .map(permissions -> permissions.stream()
                    .map(p -> (GrantedAuthority) () -> "PERMISSION_" + p.name())
                    .toList()
                )
                .flatMap(authorities -> {
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                        idUser, null, authorities
                    );
                    return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                })
            );
    }
}
