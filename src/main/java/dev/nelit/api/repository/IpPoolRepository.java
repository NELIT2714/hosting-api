package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.IpPool;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpPoolRepository extends ReactiveCrudRepository<IpPool, Long> {
}
