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

    @Column("cert_fingerprint")
    private String certFingerprint;

    @Column("cert_issued_at")
    private Instant certIssuedAt;

    @Column("cert_expires_at")
    private Instant certExpiresAt;

    @Column("last_heartbeat_at")
    private Instant lastHeartbeatAt;

    @Column("revoked_at")
    private Instant revokedAt;

    @Column("revoke_reason")
    private String revokeReason;

    public boolean isEnrolled() {
        return certFingerprint != null;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }
}
