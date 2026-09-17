package dev.nelit.api.services.user;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface UserBalanceService {
    Mono<Void> deduct(long idUser, BigDecimal amount);
}
