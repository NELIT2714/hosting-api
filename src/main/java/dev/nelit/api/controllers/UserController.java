package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.user.ChangePassword;
import dev.nelit.api.dto.request.user.Register;
import dev.nelit.api.dto.response.UserResponse;
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
    public Mono<UserResponse> create(@RequestBody @Valid Register registerDTO) {
        return userService.create(registerDTO);
    }

    @PatchMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> changePassword(@RequestBody @Valid ChangePassword changePasswordDTO) {
        return Mono.deferContextual(ctx -> {
            Long idUser = ctx.get("id_user");
            return userService.changePassword(idUser, changePasswordDTO);
        });
    }
}
