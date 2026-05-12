package dev.nelit.api.repository.admin;

import dev.nelit.api.domain.entity.admin.Admin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AdminRepository extends ReactiveCrudRepository<Admin, Long> {
    Mono<Admin> findByIdUser(Long idUser);
}
