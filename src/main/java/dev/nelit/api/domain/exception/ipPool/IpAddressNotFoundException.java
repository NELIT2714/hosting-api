package dev.nelit.api.domain.exception.ipPool;

import dev.nelit.api.domain.exception.DomainException;

public class IpAddressNotFoundException extends DomainException {
    public IpAddressNotFoundException() {
        super("IP_ADDRESS_NOT_FOUND", "Ip address not found");
    }
}
