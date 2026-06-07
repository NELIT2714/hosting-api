package dev.nelit.api.repository.vm;

import dev.nelit.api.domain.entity.vm.Vm;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Repository
public interface VmRepository extends ReactiveCrudRepository<Vm, Long> {
    Flux<Vm> findAllByIdUser(Long idUser);
    Flux<Vm> findAllByExpiresAtBeforeAndIsBlockedFalseAndIsActiveTrue(Instant now);
}
