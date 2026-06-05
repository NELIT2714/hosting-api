package dev.nelit.api.domain.exception.payment;

import dev.nelit.api.domain.exception.DomainException;

public class GatewayNotImplemented extends DomainException {
    public GatewayNotImplemented() {
        super("GATEWAY_NOT_IMPLEMENTED", "Gateway not implemented");
    }
}
