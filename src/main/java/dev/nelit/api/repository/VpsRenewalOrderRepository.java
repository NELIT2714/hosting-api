package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.vps.VpsRenewalOrder;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface VpsRenewalOrderRepository extends ReactiveCrudRepository<VpsRenewalOrder, Long> {
    Mono<VpsRenewalOrder> findByIdPayment(Long idPayment);
}