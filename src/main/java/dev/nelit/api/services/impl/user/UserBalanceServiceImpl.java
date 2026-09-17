package dev.nelit.api.services.impl.user;

import dev.nelit.api.domain.exception.user.InsufficientBalanceException;
import dev.nelit.api.repository.UserRepository;
import dev.nelit.api.services.user.UserBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserBalanceServiceImpl implements UserBalanceService {

    private final UserRepository userRepository;

    @Override
    public Mono<Void> credit(long idUser, BigDecimal amount) {
        return userRepository.creditBalance(idUser, amount).then();
    }

    @Override
    public Mono<Void> deduct(long idUser, BigDecimal amount) {
        return userRepository.deductBalance(idUser, amount)
            .defaultIfEmpty(0)
            .flatMap(rowsUpdated -> rowsUpdated == 0
                ? Mono.error(new InsufficientBalanceException())
                : Mono.empty());
    }
}
