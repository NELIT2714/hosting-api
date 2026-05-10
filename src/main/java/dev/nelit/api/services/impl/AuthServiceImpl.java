package dev.nelit.api.services.impl;

import dev.nelit.api.dto.request.user.Login;
import dev.nelit.api.domain.exception.InvalidCredentialsException;
import dev.nelit.api.services.AuthService;
import dev.nelit.api.services.JwtService;
import dev.nelit.api.services.UserService;
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

    @Override
    public Mono<String> login(Login loginDTO) {
        return userService.findByEmail(loginDTO.email())
            .flatMap(user -> {
                if (!passwordEncoder.matches(loginDTO.password(), user.getPasswordHash())) {
                    return Mono.error(new InvalidCredentialsException());
                }
                return Mono.just(jwtService.generate(user.getIdUser()));
            });
    }
}
