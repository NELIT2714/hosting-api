package dev.nelit.api.repository.admin;

import dev.nelit.api.domain.entity.admin.AdminPermission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AdminPermissionRepository extends ReactiveCrudRepository<AdminPermission, Long> {
    Flux<AdminPermission> findByIdAdmin(Long idAdmin);
}
