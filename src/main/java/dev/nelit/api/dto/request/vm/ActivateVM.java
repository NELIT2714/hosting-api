package dev.nelit.api.dto.request.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.domain.exception.vm.PasswordOrSSHRequired;

public record ActivateVM(String password, @JsonProperty("ssh_key") String sshKey) {

    public ActivateVM {
        if ((password == null || password.isBlank()) && (sshKey == null || sshKey.isBlank())) {
            throw new PasswordOrSSHRequired();
        }
    }
}
