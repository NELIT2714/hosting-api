package dev.nelit.api.domain.exception.plan;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PlanNameAlreadyTakenException extends DomainException {
    public PlanNameAlreadyTakenException() {
        super("PLAN_NAME_ALREADY_TAKEN", "The plan name is already taken", HttpStatus.CONFLICT);
    }
}
