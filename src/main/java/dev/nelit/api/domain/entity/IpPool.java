package dev.nelit.api.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "ip_pool")
public class IpPool {

    @Id
    @Column("id_ip")
    private Long idIp;

    @Column("id_node")
    private Long idNode;

    @Column("id_vm")
    private Long idVm;

    @Column("ip_address")
    private String ipAddress;

}
