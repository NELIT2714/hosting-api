package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.user.Login;
import dev.nelit.api.services.AuthService;
import dev.nelit.api.services.CookieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Tag(name = "Auth", description = "Authentication and session management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @Operation(
        summary = "Log in",
        description = "Authenticates a user with email and password. On success, sets an HTTP-only cookie containing the JWT token.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Authenticated successfully — JWT cookie set"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> login(@RequestBody Login loginDTO, ServerWebExchange exchange) {
        return authService.login(loginDTO)
            .flatMap(token -> {
                cookieService.setTokenCookie(exchange, token);
                return exchange.getResponse().setComplete();
            });
    }

//    @DeleteMapping("/logout")
//    @ResponseStatus(HttpStatus.OK)
//    public Mono<Void> logout(ServerWebExchange exchange) {
//        cookieService.clearTokenCookie(exchange);
//        return exchange.getResponse().setComplete();
//    }

}
