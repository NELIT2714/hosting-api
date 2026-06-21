package dev.nelit.api.dto.request.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nelit.api.domain.exception.vm.PasswordOrSSHRequired;
import jakarta.validation.constraints.NotNull;

public record ReinstallVM(
    @JsonProperty("id_os_image") @NotNull Long idOsImage,
    @JsonProperty("password") String password,
    @JsonProperty("ssh_key") String sshKey
) {
    public ReinstallVM {
        if ((password == null || password.isBlank()) && (sshKey == null || sshKey.isBlank())) {
            throw new PasswordOrSSHRequired();
        }
    }
}
