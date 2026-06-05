package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.VM;
import dev.nelit.api.dto.response.VMResponse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface VMRepository extends ReactiveCrudRepository<VM, Long> {
    Flux<VM> findAllByIdUser(Long idUser);
}
