package dev.nelit.api.services;

import dev.nelit.api.domain.entity.admin.Admin;
import dev.nelit.api.dto.request.admin.CreateAdmin;
import dev.nelit.api.dto.response.AdminResponse;
import dev.nelit.api.enums.AdminPermission;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface AdminService {
    Mono<AdminResponse> create(CreateAdmin createAdminDTO);
    Mono<AdminResponse> update(Long idAdmin, Set<AdminPermission> permissions);
    Mono<Void> delete(Long idAdmin);
    Mono<List<AdminPermission>> getPermissions(Long idAdmin);
    Mono<Boolean> hasPermission(Long idUser, AdminPermission permission);

    Mono<Admin> getByUserId(Long idUser);
}
