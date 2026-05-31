package dev.nelit.api.domain.exception.ipPool;

import dev.nelit.api.domain.exception.DomainException;

public class NoAvailableAddressesException extends DomainException {
    public NoAvailableAddressesException() {
        super("NO_AVAILABLE_ADDRESSES", "No available addresses");
    }
}