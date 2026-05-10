package dev.nelit.api.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table(name = "nodes")
public class Node {

    @Id
    @Column("id_node")
    private Long idNode;

    @Column("node_name")
    private String nodeName;

    @Column("ip_address")
    private String ipAddress;

    @Column("grpc_port")
    private Integer grpcPort;

    @Column("location")
    private String location;

    @Column("is_active")
    private Boolean isActive;

    @Column("created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

}
