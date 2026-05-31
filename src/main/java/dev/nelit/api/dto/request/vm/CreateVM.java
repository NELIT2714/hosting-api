package dev.nelit.api.dto.request.vm;

import dev.nelit.api.domain.exception.vm.PasswordOrSSHRequired;

public record CreateVM(
    Long idPlan,
    Long idOsImage,
    String password,
    String sshKey
) {
    public CreateVM {
        if ((password == null || password.isBlank()) && (sshKey == null || sshKey.isBlank())) {
            throw new PasswordOrSSHRequired();
        }
    }
}
