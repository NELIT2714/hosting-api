package dev.nelit.api.controllers;

import dev.nelit.api.dto.user.request.Register;
import dev.nelit.api.dto.user.response.UserResponse;
import dev.nelit.api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponse> create(@RequestBody @Valid Register request) {
        return userService.create(request);
    }
}
