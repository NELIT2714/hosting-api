package dev.nelit.api.domain.exception.plan;

import dev.nelit.api.domain.exception.DomainException;

public class PlanNotFound extends DomainException {
    public PlanNotFound() {
        super("PLAN_NOT_FOUND", "Plan not found");
    }
}
