package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.admin.Admin;
import dev.nelit.api.domain.entity.admin.AdminPermission;
import dev.nelit.api.domain.exception.admin.AdminNotFoundException;
import dev.nelit.api.domain.exception.user.UserNotFoundException;
import dev.nelit.api.dto.request.admin.CreateAdmin;
import dev.nelit.api.dto.response.AdminResponse;
import dev.nelit.api.enums.AdminPermissions;
import dev.nelit.api.mappers.AdminMapper;
import dev.nelit.api.repository.UserRepository;
import dev.nelit.api.repository.admin.AdminPermissionRepository;
import dev.nelit.api.repository.admin.AdminRepository;
import dev.nelit.api.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AdminPermissionRepository adminPermissionRepository;
    private final UserRepository userRepository;
    private final AdminMapper adminMapper;

    @Override
    public Mono<AdminResponse> create(CreateAdmin dto) {
        return userRepository.findById(dto.idUser())
            .switchIfEmpty(Mono.error(new UserNotFoundException()))
            .flatMap(_ -> adminRepository.save(adminMapper.toAdmin(dto)))
            .flatMap(saved -> {
                List<AdminPermission> permissions = dto.permissions().stream()
                    .map(p -> adminMapper.toPermission(saved.getIdAdmin(), p))
                    .toList();

                return adminPermissionRepository.saveAll(permissions)
                    .map(AdminPermission::getPermission)
                    .collectList()
                    .map(perms -> adminMapper.toResponse(saved, perms));
            });
    }

    @Override
    public Mono<AdminResponse> update(Long idAdmin, Set<AdminPermissions> permissionsToUpdate) {
        return adminRepository.findById(idAdmin)
            .switchIfEmpty(Mono.error(new AdminNotFoundException()))
            .flatMap(admin -> adminPermissionRepository.deleteAllByIdAdmin(idAdmin)
                .thenMany(adminPermissionRepository.saveAll(
                    permissionsToUpdate.stream()
                        .map(p -> adminMapper.toPermission(idAdmin, p))
                        .toList()
                ))
                .map(AdminPermission::getPermission)
                .collectList()
                .map(perms -> adminMapper.toResponse(admin, perms))
            );
    }

    @Override
    public Mono<Void> delete(Long idAdmin) {
        return adminRepository.deleteById(idAdmin);
    }

    @Override
    public Mono<List<AdminPermissions>> getPermissions(Long idAdmin) {
        return adminPermissionRepository.findByIdAdmin(idAdmin)
            .switchIfEmpty(Mono.error(new AdminNotFoundException()))
            .map(AdminPermission::getPermission)
            .collectList();
    }


    @Override
    public Mono<Boolean> hasPermission(Long idUser, AdminPermissions permission) {
        return adminRepository.findByIdUser(idUser)
            .flatMap(admin -> adminPermissionRepository.findByIdAdmin(admin.getIdAdmin())
                .map(AdminPermission::getPermission)
                .any(p -> p.equals(permission.name()))
            )
            .defaultIfEmpty(false);
    }

    @Override
    public Mono<Admin> getByUserId(Long idUser) {
        return adminRepository.findByIdUser(idUser)
            .switchIfEmpty(Mono.error(new AdminNotFoundException()));
    }
}
