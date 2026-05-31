package dev.nelit.api.domain.exception.ipPool;

import dev.nelit.api.domain.exception.DomainException;

public class IpAddressAlreadyAssignedException extends DomainException {
    public IpAddressAlreadyAssignedException() {
        super("IP_ADDRESS_ALREADY_ASSIGNED", "IP is already assigned to a VM");
    }
}
