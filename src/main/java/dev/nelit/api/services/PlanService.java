package dev.nelit.api.services;

import dev.nelit.api.dto.request.plan.CreatePlan;
import dev.nelit.api.dto.request.plan.UpdatePlan;
import dev.nelit.api.dto.response.plan.PlanResponse;
import reactor.core.publisher.Mono;

public interface PlanService {
    Mono<PlanResponse> create(CreatePlan createPlanDTO);
    Mono<PlanResponse> update(Long id, UpdatePlan updatePlanDTO);
    Mono<PlanResponse> delete(Long id);
}
