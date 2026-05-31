package dev.nelit.api.domain.exception.vm;

import dev.nelit.api.domain.exception.DomainException;

public class PasswordOrSSHRequired extends DomainException {
    public PasswordOrSSHRequired() {
        super("PASSWORD_OR_SSH_KEY_REQUIRED", "Password or SSH Key required");
    }
}
