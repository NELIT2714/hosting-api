package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.Plan;
import dev.nelit.api.domain.exception.plan.PlanNameAlreadyTakenException;
import dev.nelit.api.domain.exception.plan.PlanNotFoundException;
import dev.nelit.api.dto.request.plan.CreatePlan;
import dev.nelit.api.dto.request.plan.UpdatePlan;
import dev.nelit.api.dto.response.PlanResponse;
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
    public Mono<PlanResponse> getById(Long planId) {
        return planRepository.findById(planId)
            .switchIfEmpty(Mono.error(new PlanNotFoundException()))
            .map(planMapper::toResponse);
    }

    @Override
    public Mono<PlanResponse> create(CreatePlan createPlanDTO) {
        Plan plan = Plan.builder()
            .planName(createPlanDTO.planName())
            .ramMb(createPlanDTO.ramMb())
            .vcpus(createPlanDTO.vcpus())
            .diskGb(createPlanDTO.diskGb())
            .pricePerMonth(createPlanDTO.pricePerMonth())
            .isActive(createPlanDTO.isActive())
            .build();

        return planRepository.save(plan)
            .map(planMapper::toResponse)
            .onErrorMap(DuplicateKeyException.class, _ -> new PlanNameAlreadyTakenException());
    }

    @Override
    public Mono<PlanResponse> update(Long planId, UpdatePlan updatePlanDTO) {
        return planRepository.findById(planId)
            .switchIfEmpty(Mono.error(new PlanNotFoundException()))
            .flatMap(plan -> {
                planMapper.update(updatePlanDTO, plan);
                return planRepository.save(plan);
            })
            .map(planMapper::toResponse);
    }

    @Override
    public Mono<Void> delete(Long planId) {
        return planRepository.findById(planId)
            .switchIfEmpty(Mono.error(new PlanNotFoundException()))
            .flatMap(planRepository::delete);
    }
}
