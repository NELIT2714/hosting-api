package dev.nelit.api.services.impl;

import dev.nelit.api.domain.entity.admin.Admin;
import dev.nelit.api.domain.entity.admin.AdminPermission;
import dev.nelit.api.domain.exception.admin.AdminNotFoundException;
import dev.nelit.api.domain.exception.user.UserNotFoundException;
import dev.nelit.api.dto.request.admin.CreateAdmin;
import dev.nelit.api.dto.response.AdminResponse;
import dev.nelit.api.enums.AdminPermissions;
import dev.nelit.api.repository.UserRepository;
import dev.nelit.api.repository.admin.AdminPermissionRepository;
import dev.nelit.api.repository.admin.AdminRepository;
import dev.nelit.api.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AdminPermissionRepository adminPermissionRepository;
    private final UserRepository userRepository;

    @Override
    public Mono<AdminResponse> create(CreateAdmin createAdminDTO) {
        return userRepository.findById(createAdminDTO.idUser())
            .switchIfEmpty(Mono.error(new UserNotFoundException()))
            .flatMap(_ -> {
                Admin admin = Admin.builder()
                    .idUser(createAdminDTO.idUser())
                    .build();

                return adminRepository.save(admin)
                    .flatMap(savedAdmin -> {
                        List<AdminPermission> permissions = createAdminDTO.permissions().stream()
                            .map(p -> AdminPermission.builder()
                                .idAdmin(savedAdmin.getIdAdmin())
                                .permission(p.name())
                                .build()
                            )
                            .toList();

                        return adminPermissionRepository.saveAll(permissions)
                            .map(AdminPermission::getPermission)
                            .collectList()
                            .map(perms -> new AdminResponse(
                                savedAdmin.getIdAdmin(),
                                savedAdmin.getIdUser(),
                                perms,
                                savedAdmin.getCreatedAt()
                            ));
                    });
            });
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
            .map(AdminPermissions::valueOf)
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
