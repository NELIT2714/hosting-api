package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.user.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE users SET balance = balance - :amount WHERE id_user = :idUser AND balance >= :amount")
    Mono<Integer> deductBalance(long idUser, BigDecimal amount);
}
