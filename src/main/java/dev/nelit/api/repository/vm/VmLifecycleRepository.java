package dev.nelit.api.repository.vm;

import dev.nelit.api.domain.entity.vm.VmLifecycle;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface VmLifecycleRepository extends ReactiveCrudRepository<VmLifecycle, Long> {
    Flux<VmLifecycle> findAllByDeleteAtBefore(Instant now);
    Mono<Void> deleteByIdVm(Long idVm);
}
