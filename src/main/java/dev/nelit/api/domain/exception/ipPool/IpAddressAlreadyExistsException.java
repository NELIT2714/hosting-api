package dev.nelit.api.domain.exception.ipPool;

import dev.nelit.api.domain.exception.DomainException;

public class IpAddressAlreadyExistsException extends DomainException {
    public IpAddressAlreadyExistsException() {
        super("IP_ADDRESS_ALREADY_EXISTS", "Ip address already exists");
    }
}
