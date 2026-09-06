package dev.nelit.api.services.impl;

import dev.nelit.api.services.CookieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements CookieService {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final String ACCESS_COOKIE_NAME = "access_token";

    @Value("${jwt.token-expiration-minutes}")
    private int accessTokenMinutes;

    @Value("${jwt.refresh-expiration-days}")
    private int refreshTokenDays;

    private final Environment environment;

    @Override
    public void setTokenCookie(ServerWebExchange exchange, String token) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_COOKIE_NAME, token)
            .httpOnly(true)
            .secure(environment.acceptsProfiles(Profiles.of("prod")))
            .path("/")
            .maxAge(Duration.ofMinutes(accessTokenMinutes))
            .sameSite("Strict")
            .build();
        exchange.getResponse().addCookie(cookie);
    }

    @Override
    public void clearTokenCookie(ServerWebExchange exchange) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_COOKIE_NAME, "")
            .httpOnly(true)
            .secure(environment.acceptsProfiles(Profiles.of("prod")))
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();
        exchange.getResponse().addCookie(cookie);
    }

    @Override
    public void setRefreshCookie(ServerWebExchange exchange, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
            .httpOnly(true)
            .secure(environment.acceptsProfiles(Profiles.of("prod")))
            .path("/v1/auth")
            .maxAge((long) refreshTokenDays * 24 * 60 * 60)
            .sameSite("Strict")
            .build();
        exchange.getResponse().addCookie(cookie);
    }

    @Override
    public void clearRefreshCookie(ServerWebExchange exchange) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
            .httpOnly(true)
            .secure(environment.acceptsProfiles(Profiles.of("prod")))
            .path("/v1/auth")
            .maxAge(0)
            .sameSite("Strict")
            .build();
        exchange.getResponse().addCookie(cookie);
    }

    @Override
    public String getRefreshToken(ServerWebExchange exchange) {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst(REFRESH_COOKIE_NAME);
        return cookie != null ? cookie.getValue() : null;
    }
}
