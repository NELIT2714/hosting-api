package dev.nelit.api.domain.exception.node;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class NodeNotFoundException extends DomainException {
    public NodeNotFoundException() {
        super("NODE_NOT_FOUND", "Node not found", HttpStatus.NOT_FOUND);
    }
}
