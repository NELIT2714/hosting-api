package dev.nelit.api.domain.exception.plan;

import dev.nelit.api.domain.exception.DomainException;

public class PlanNotFoundException extends DomainException {
    public PlanNotFoundException() {
        super("PLAN_NOT_FOUND", "Plan not found");
    }
}
