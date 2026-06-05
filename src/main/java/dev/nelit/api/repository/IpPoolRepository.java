package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.IpPool;
import dev.nelit.api.dto.response.IpPoolResponse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IpPoolRepository extends ReactiveCrudRepository<IpPool, Long> {
    Mono<IpPool> findFirstByIdVmIsNullAndIdNodeOrderByIdIpAsc(Long idNode);
    Mono<IpPool> findByIdVm(Long idVm);
    Mono<Boolean> existsByIdVmIsNull();
}
