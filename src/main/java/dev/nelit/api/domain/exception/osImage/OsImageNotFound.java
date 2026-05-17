package dev.nelit.api.domain.exception.osImage;

import dev.nelit.api.domain.exception.DomainException;

public class OsImageNotFound extends DomainException {
    public OsImageNotFound() {
        super("OS_IMAGE_NOT_FOUND",   "OS image not found");
    }
}
