package dev.nelit.api.domain.entity.admin;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table(name = "admins")
public class Admin {

    @Id
    @Column("id_admin")
    private Long idAdmin;

    @Column("id_user")
    private Long idUser;

    @Column("created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

}
