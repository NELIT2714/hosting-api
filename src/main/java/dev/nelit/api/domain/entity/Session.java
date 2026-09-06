package dev.nelit.api.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("sessions")
@Data
@Builder
public class Session {

    @Id
    @Column("id_session")
    private Long idSession;

    @Column("id_user")
    private Long idUser;

    @Column("refresh_token_hash")
    private String refreshTokenHash;

    @Column("created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column("expires_at")
    private Instant expiresAt;

    @Column("revoked_at")
    private Instant revokedAt;

    @Column("revoke_reason")
    private String revokeReason;
}
