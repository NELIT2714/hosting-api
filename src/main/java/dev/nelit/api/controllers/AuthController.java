package dev.nelit.api.controllers;

import dev.nelit.api.dto.user.request.Login;
import dev.nelit.api.services.AuthService;
import dev.nelit.api.services.CookieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/login")
    public Mono<Void> login(@RequestBody Login loginDTO, ServerWebExchange exchange) {
        return authService.login(loginDTO)
            .flatMap(token -> {
                cookieService.setTokenCookie(exchange, token);
                return exchange.getResponse().setComplete();
            });
    }

    @DeleteMapping("/logout")
    public Mono<Void> logout(ServerWebExchange exchange) {
        cookieService.clearTokenCookie(exchange);
        return exchange.getResponse().setComplete();
    }

}
