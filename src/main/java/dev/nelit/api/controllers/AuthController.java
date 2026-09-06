package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.user.Login;
import dev.nelit.api.services.AuthService;
import dev.nelit.api.services.CookieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Tag(name = "Auth", description = "Authentication and session management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @Operation(
        summary = "Log in",
        description = "Authenticates a user with email and password. On success, sets HTTP-only " +
            "access and refresh token cookies.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Authenticated successfully — cookies set"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> login(@RequestBody @Valid Login loginDTO, ServerWebExchange exchange) {
        return authService.login(loginDTO)
            .flatMap(tokens -> {
                cookieService.setTokenCookie(exchange, tokens.accessToken());
                cookieService.setRefreshCookie(exchange, tokens.refreshToken());
                return exchange.getResponse().setComplete();
            });
    }

    @Operation(
        summary = "Refresh access token",
        description = "Uses the refresh token cookie to issue a new access token cookie, " +
            "without requiring the user to log in again.",
        responses = {
            @ApiResponse(responseCode = "200", description = "New access token cookie set"),
            @ApiResponse(responseCode = "401", description = "Missing, invalid, expired, or revoked refresh token",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> refresh(ServerWebExchange exchange) {
        String refreshToken = cookieService.getRefreshToken(exchange);
        if (refreshToken == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "no refresh token"));
        }

        return authService.refresh(refreshToken)
            .flatMap(accessToken -> {
                cookieService.setTokenCookie(exchange, accessToken);
                return exchange.getResponse().setComplete();
            });
    }

    @Operation(
        summary = "Log out",
        description = "Revokes the current refresh token session and clears both cookies.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Logged out successfully")
        }
    )
    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> logout(ServerWebExchange exchange) {
        String refreshToken = cookieService.getRefreshToken(exchange);

        Mono<Void> revoke = refreshToken != null
            ? authService.logout(refreshToken)
            : Mono.empty();

        return revoke.then(Mono.defer(() -> {
            cookieService.clearTokenCookie(exchange);
            cookieService.clearRefreshCookie(exchange);
            return exchange.getResponse().setComplete();
        }));
    }
}
