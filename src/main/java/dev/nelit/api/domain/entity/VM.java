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
public class VM {

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

    @Column("ip_address")
    private String ip_address;

    @Column("created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

}
