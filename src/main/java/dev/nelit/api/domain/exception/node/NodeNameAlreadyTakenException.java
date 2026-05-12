package dev.nelit.api.domain.exception.node;

import dev.nelit.api.domain.exception.DomainException;

public class NodeNameAlreadyTakenException extends DomainException {
    public NodeNameAlreadyTakenException() {
        super("NODE_NAME_ALREADY_TAKEN", "Node name is already taken");
    }
}
