package dev.nelit.api.services;

import dev.nelit.api.domain.entity.admin.Admin;
import dev.nelit.api.dto.request.admin.CreateAdmin;
import dev.nelit.api.dto.response.AdminResponse;
import dev.nelit.api.enums.AdminPermissions;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AdminService {
    Mono<AdminResponse> create(CreateAdmin createAdminDTO);
    Mono<Void> delete(Long idAdmin);
    Mono<List<AdminPermissions>> getPermissions(Long idAdmin);
    Mono<Boolean> hasPermission(Long idUser, AdminPermissions permission);
    Mono<Admin> getByUserId(Long idUser);
}
