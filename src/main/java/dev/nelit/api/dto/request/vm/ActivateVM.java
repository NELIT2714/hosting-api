package dev.nelit.api.dto.request.vm;

import dev.nelit.api.domain.exception.vm.PasswordOrSSHRequired;

public record ActivateVM(String password, String sshKey) {

    public ActivateVM {
        if ((password == null || password.isBlank()) && (sshKey == null || sshKey.isBlank())) {
            throw new PasswordOrSSHRequired();
        }
    }
}
