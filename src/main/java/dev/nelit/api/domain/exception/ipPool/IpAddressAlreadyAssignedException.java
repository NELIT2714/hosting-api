package dev.nelit.api.domain.exception.ipPool;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class IpAddressAlreadyAssignedException extends DomainException {
    public IpAddressAlreadyAssignedException() {
        super("IP_ADDRESS_ALREADY_ASSIGNED", "IP is already assigned to a VM", HttpStatus.CONFLICT);
    }
}
