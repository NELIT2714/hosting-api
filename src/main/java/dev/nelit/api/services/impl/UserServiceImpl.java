package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.user.User;
import dev.nelit.api.dto.request.user.ChangePassword;
import dev.nelit.api.dto.request.user.Register;
import dev.nelit.api.dto.response.UserResponse;
import dev.nelit.api.domain.exception.user.CurrentPasswordIncorrectException;
import dev.nelit.api.domain.exception.user.EmailAlreadyExistsException;
import dev.nelit.api.domain.exception.user.PasswordsDontMatchException;
import dev.nelit.api.domain.exception.user.UserNotFoundException;
import dev.nelit.api.mappers.UserMapper;
import dev.nelit.api.repository.UserRepository;
import dev.nelit.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Mono<UserResponse> create(Register dto) {
        User user = User.builder()
            .email(dto.email())
            .passwordHash(passwordEncoder.encode(dto.password()))
            .build();

        return userRepository.save(user)
            .map(userMapper::toResponse)
            .onErrorMap(DuplicateKeyException.class, _ -> new EmailAlreadyExistsException());
    }

    @Override
    public Mono<Void> changePassword(Long idUser, ChangePassword changePasswordDTO) {
        return userRepository.findById(idUser)
            .switchIfEmpty(Mono.error(new UserNotFoundException()))
            .flatMap(user -> {
                if (!passwordEncoder.matches(changePasswordDTO.currentPassword(), user.getPasswordHash()))
                    return Mono.error(new CurrentPasswordIncorrectException());

                if (!changePasswordDTO.newPassword().equals(changePasswordDTO.repeatNewPassword()))
                    return Mono.error(new PasswordsDontMatchException());

                user.setPasswordHash(passwordEncoder.encode(changePasswordDTO.newPassword()));
                return userRepository.save(user);
            })
            .then();
    }

    @Override
    public Mono<Void> delete(Long idUser) {
        return userRepository.findById(idUser)
            .switchIfEmpty(Mono.error(new UserNotFoundException()))
            .flatMap(user -> {
                user.setDeletedAt(Instant.now());
                return userRepository.save(user);
            })
            .then();
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
            .filter(user -> user.getDeletedAt() == null)
            .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }
}
