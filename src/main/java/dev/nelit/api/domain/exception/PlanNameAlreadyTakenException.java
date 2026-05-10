package dev.nelit.api.domain.exception;

public class PlanNameAlreadyTakenException extends DomainException {
    public PlanNameAlreadyTakenException() {
        super("PLAN_NAME_ALREADY_TAKEN", "The plan name is already taken");
    }
}
