package dev.nelit.api.services;

import dev.nelit.api.domain.entity.user.User;
import dev.nelit.api.dto.user.request.ChangePassword;
import dev.nelit.api.dto.user.request.Register;
import dev.nelit.api.dto.user.response.UserResponse;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserResponse> create(Register registerDTO);
    Mono<UserResponse> changePassword(Long idUser, ChangePassword changePasswordDTO);
    Mono<Void> delete(Long idUser);

    Mono<User> findByEmail(String email);
}
