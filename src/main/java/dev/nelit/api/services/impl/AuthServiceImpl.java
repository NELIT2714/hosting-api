package dev.nelit.api.services.impl;

import dev.nelit.api.domain.exception.user.InvalidPasswordException;
import dev.nelit.api.dto.request.user.Login;
import dev.nelit.api.dto.response.AuthTokens;
import dev.nelit.api.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;
    private final SessionService sessionService;

    @Override
    public Mono<AuthTokens> login(Login loginDTO) {
        return userService.findByEmail(loginDTO.email())
            .flatMap(user -> {
                if (!passwordEncoder.matches(loginDTO.password(), user.getPasswordHash())) {
                    return Mono.error(new InvalidPasswordException());
                }

                return resolveRole(user.getIdUser())
                    .flatMap(role -> sessionService.create(user.getIdUser())
                        .map(refreshToken -> new AuthTokens(
                            jwtService.generate(user.getIdUser(), role),
                            refreshToken
                        )));
            });
    }

    @Override
    public Mono<String> refresh(String rawRefreshToken) {
        return sessionService.findActiveByRawToken(rawRefreshToken)
            .flatMap(session -> resolveRole(session.getIdUser())
                .map(role -> jwtService.generate(session.getIdUser(), role)));
    }

    @Override
    public Mono<Void> logout(String rawRefreshToken) {
        return sessionService.findActiveByRawToken(rawRefreshToken)
            .flatMap(session -> sessionService.revoke(session.getIdSession(), "logout"));
    }

    private Mono<String> resolveRole(Long idUser) {
        return adminService.getByUserId(idUser)
            .map(_ -> "ADMIN")
            .switchIfEmpty(Mono.just("USER"));
    }
}
