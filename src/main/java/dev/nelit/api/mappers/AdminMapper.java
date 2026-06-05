package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.admin.Admin;
import dev.nelit.api.dto.request.admin.CreateAdmin;
import dev.nelit.api.dto.response.AdminResponse;
import dev.nelit.api.enums.AdminPermission;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminMapper {

    public Admin toAdmin(CreateAdmin dto) {
        return Admin.builder()
            .idUser(dto.idUser())
            .build();
    }

    public dev.nelit.api.domain.entity.admin.AdminPermission toPermission(Long idAdmin, AdminPermission permission) {
        return dev.nelit.api.domain.entity.admin.AdminPermission.builder()
            .idAdmin(idAdmin)
            .permission(permission)
            .build();
    }

    public AdminResponse toResponse(Admin admin, List<AdminPermission> permissions) {
        return new AdminResponse(
            admin.getIdAdmin(),
            admin.getIdUser(),
            permissions,
            admin.getCreatedAt()
        );
    }
}
