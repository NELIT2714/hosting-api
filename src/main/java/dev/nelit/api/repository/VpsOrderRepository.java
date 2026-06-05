package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.VpsOrder;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface VpsOrderRepository extends ReactiveCrudRepository<VpsOrder, Long> {
    Mono<VpsOrder> findByIdPayment(Long idPayment);
}
