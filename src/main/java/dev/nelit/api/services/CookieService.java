package dev.nelit.api.services;

import org.springframework.web.server.ServerWebExchange;

public interface CookieService {
    void setTokenCookie(ServerWebExchange exchange, String token);
    void clearTokenCookie(ServerWebExchange exchange);
    void setRefreshCookie(ServerWebExchange exchange, String refreshToken);
    void clearRefreshCookie(ServerWebExchange exchange);
    String getRefreshToken(ServerWebExchange exchange);
}
