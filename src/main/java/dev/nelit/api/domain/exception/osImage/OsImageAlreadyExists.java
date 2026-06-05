package dev.nelit.api.domain.exception.osImage;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class OsImageAlreadyExists extends DomainException {
    public OsImageAlreadyExists() {
        super("OS_IMAGE_ALREADY_EXISTS", "OS image already exists", HttpStatus.CONFLICT);
    }
}
