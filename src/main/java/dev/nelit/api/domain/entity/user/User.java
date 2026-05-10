package dev.nelit.api.domain.entity.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table(name = "users")
public class User {

    @Id
    @Column("id_user")
    private Long idUser;

    @Column("email")
    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

}