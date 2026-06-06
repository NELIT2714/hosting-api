package dev.nelit.api.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table(name = "virtual_machines")
public class Vm {

    @Id
    @Column("id_vm")
    private Long idVM;

    @Column("id_user")
    private Long idUser;

    @Column("id_node")
    private Long idNode;

    @Column("id_plan")
    private Long idPlan;

    @Column("vm_name")
    private String vmName;

    @Column("uuid")
    private String uuid;

    @Column("is_active")
    @Builder.Default
    private Boolean isActive = false;

    @Column("is_blocked")
    @Builder.Default
    private Boolean isBlocked = false;

    @Column("created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column("expires_at")
    private Instant expiresAt;

}
