package dev.nelit.api.domain.entity.admin;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "admin_permissions")
public class AdminPermission {

    @Column("id_admin")
    private Long idAdmin;

    @Column("permission")
    private dev.nelit.api.enums.AdminPermission permission;

}
