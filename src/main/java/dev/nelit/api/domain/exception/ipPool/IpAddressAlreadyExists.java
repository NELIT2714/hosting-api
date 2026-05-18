package dev.nelit.api.domain.exception.ipPool;

import dev.nelit.api.domain.exception.DomainException;

public class IpAddressAlreadyExists extends DomainException {
    public IpAddressAlreadyExists() {
        super("IP_ADDRESS_ALREADY_EXISTS", "Ip address already exists");
    }
}
