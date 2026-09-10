package dev.nelit.api.services;

import dev.nelit.api.domain.entity.user.User;
import dev.nelit.api.dto.request.user.ChangePassword;
import dev.nelit.api.dto.request.user.Register;
import dev.nelit.api.dto.response.UserResponse;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserResponse> getById(long idUser);

    Mono<UserResponse> create(Register registerDTO);
    Mono<Void> changePassword(long idUser, ChangePassword changePasswordDTO);
    Mono<Void> delete(long idUser);

    Mono<User> findByEmail(String email);

}
