package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.VM;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VMRepository extends ReactiveCrudRepository<VM, Long> {
}
