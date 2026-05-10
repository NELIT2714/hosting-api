package dev.nelit.api;

import dev.nelit.api.dto.user.request.Register;
import dev.nelit.api.domain.entity.user.User;
import dev.nelit.api.repository.UserRepository;
import dev.nelit.api.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_shouldSaveUserAndReturnResponse() {
        Register request = new Register("test@test.com", "password123", "password123");

        when(passwordEncoder.encode(any())).thenReturn("hashed_password");

        User savedUser = User.builder()
            .idUser(1L)
            .email("test@test.com")
            .passwordHash("hashed_password")
            .createdAt(Instant.now())
            .build();

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(userService.create(request))
            .assertNext(response -> {
                assertThat(response.getIdUser()).isEqualTo(1L);
                assertThat(response.getEmail()).isEqualTo("test@test.com");
            })
            .verifyComplete();
    }
}
