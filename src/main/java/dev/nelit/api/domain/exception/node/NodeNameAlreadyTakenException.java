package dev.nelit.api.domain.exception.node;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class NodeNameAlreadyTakenException extends DomainException {
    public NodeNameAlreadyTakenException() {
        super("NODE_NAME_ALREADY_TAKEN", "Node name is already taken", HttpStatus.CONFLICT);
    }
}
