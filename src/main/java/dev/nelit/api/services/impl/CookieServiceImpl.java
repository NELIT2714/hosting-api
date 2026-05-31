package dev.nelit.api.services.impl;

import dev.nelit.api.services.CookieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements CookieService {

    @Value("${jwt.expiration-days}")
    private int cookieDays;

    private final Environment environment;

    @Override
    public void setTokenCookie(ServerWebExchange exchange, String token) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(environment.acceptsProfiles("prod"))
            .path("/")
            .maxAge((long) cookieDays * 24 * 60 * 60)
            .sameSite("Strict")
            .build();
        exchange.getResponse().addCookie(cookie);
    }

    @Override
    public void clearTokenCookie(ServerWebExchange exchange) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
            .httpOnly(true)
            .secure(environment.acceptsProfiles("prod"))
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();
        exchange.getResponse().addCookie(cookie);
    }
}
