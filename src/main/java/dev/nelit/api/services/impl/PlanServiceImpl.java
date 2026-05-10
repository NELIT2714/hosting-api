package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.Plan;
import dev.nelit.api.domain.exception.EmailAlreadyExistsException;
import dev.nelit.api.domain.exception.PlanNameAlreadyTakenException;
import dev.nelit.api.dto.request.plan.CreatePlan;
import dev.nelit.api.dto.request.plan.UpdatePlan;
import dev.nelit.api.dto.response.plan.PlanResponse;
import dev.nelit.api.mappers.PlanMapper;
import dev.nelit.api.repository.PlanRepository;
import dev.nelit.api.services.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final PlanMapper planMapper;

    @Override
    public Mono<PlanResponse> create(CreatePlan createPlanDTO) {
        Plan plan = Plan.builder()
            .planName(createPlanDTO.planName())
            .ramMb(createPlanDTO.ramMb())
            .vcpus(createPlanDTO.vcpus())
            .diskGb(createPlanDTO.diskGb())
            .pricePerMonth(createPlanDTO.pricePerMonth())
            .maxCount(createPlanDTO.maxCount())
            .maxUplinkMbps(createPlanDTO.maxUplinkMbps())
            .isActive(createPlanDTO.isActive())
            .build();

        return planRepository.save(plan)
            .map(planMapper::toResponse)
            .onErrorMap(DuplicateKeyException.class, _ -> new PlanNameAlreadyTakenException());
    }

    @Override
    public Mono<PlanResponse> update(Long id, UpdatePlan updatePlanDTO) {
        return null;
    }

    @Override
    public Mono<PlanResponse> delete(Long id) {
        return null;
    }
}
