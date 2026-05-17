package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.OsImage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OsImageRepository extends ReactiveCrudRepository<OsImage, Long> {
}
