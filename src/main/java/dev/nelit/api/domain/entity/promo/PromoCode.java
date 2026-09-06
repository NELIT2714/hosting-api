package dev.nelit.api.domain.entity.promo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table(name = "promo_codes")
public class PromoCode {

    @Id
    @Column("id_promo")
    private Long idPromo;

    @Column("code")
    private String code;

    @Column("amount_of_uses")
    private Integer amountOfUses;

    @Column("discount")
    private Integer discount;

    @Column("is_active")
    private Boolean isActive;

    @Column("expires_at")
    private Instant expiresAt;

    @Builder.Default
    @Column("created_at")
    private Instant createdAt = Instant.now();

}
