package dev.nelit.api.domain.exception.plan;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PlanNotFoundException extends DomainException {
    public PlanNotFoundException() {
        super("PLAN_NOT_FOUND", "Plan not found", HttpStatus.NOT_FOUND);
    }
}
