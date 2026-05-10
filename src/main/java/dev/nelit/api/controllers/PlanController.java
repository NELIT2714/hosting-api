package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.plan.CreatePlan;
import dev.nelit.api.dto.request.plan.UpdatePlan;
import dev.nelit.api.dto.response.plan.PlanResponse;
import dev.nelit.api.services.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plans")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PlanResponse> create(@RequestBody @Valid CreatePlan createPlanDTO) {
        return planService.create(createPlanDTO);
    }

    @PatchMapping("/{plan_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<PlanResponse> update(@RequestBody @Valid UpdatePlan updatePlanDTO, @PathVariable("plan_id") Long planId) {
        return planService.update(planId, updatePlanDTO);
    }

    @DeleteMapping("/{plan_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable("plan_id") Long planId) {
        return planService.delete(planId);
    }

}
