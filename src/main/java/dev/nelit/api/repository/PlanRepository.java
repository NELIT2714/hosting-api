package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.Plan;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PlanRepository extends ReactiveCrudRepository<Plan, Long> {
}
