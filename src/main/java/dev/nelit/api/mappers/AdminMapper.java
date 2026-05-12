package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.admin.Admin;
import dev.nelit.api.domain.entity.admin.AdminPermission;
import dev.nelit.api.dto.request.admin.CreateAdmin;
import dev.nelit.api.dto.response.AdminResponse;
import dev.nelit.api.enums.AdminPermissions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminMapper {

    public Admin toAdmin(CreateAdmin dto) {
        return Admin.builder()
            .idUser(dto.idUser())
            .build();
    }

    public AdminPermission toPermission(Long idAdmin, AdminPermissions permission) {
        return AdminPermission.builder()
            .idAdmin(idAdmin)
            .permission(permission)
            .build();
    }

    public AdminResponse toResponse(Admin admin, List<AdminPermissions> permissions) {
        return new AdminResponse(
            admin.getIdAdmin(),
            admin.getIdUser(),
            permissions,
            admin.getCreatedAt()
        );
    }
}
