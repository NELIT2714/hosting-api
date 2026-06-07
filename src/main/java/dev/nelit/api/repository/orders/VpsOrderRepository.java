package dev.nelit.api.repository.orders;

import dev.nelit.api.domain.entity.vps.VpsOrder;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface VpsOrderRepository extends ReactiveCrudRepository<VpsOrder, Long> {
    Mono<VpsOrder> findByIdPayment(Long idPayment);
    Mono<VpsOrder> findByIdVm(Long idVm);
}
