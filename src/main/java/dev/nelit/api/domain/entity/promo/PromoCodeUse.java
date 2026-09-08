package dev.nelit.api.domain.entity.promo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table(name = "promo_codes_uses")
public class PromoCodeUse {

    @Id
    @Column("id_promo_use")
    private Long idPromoUse;

    @Column("id_user")
    private Long idUser;

    @Column("id_promo")
    private Long idPromo;

    @Column("id_payment")
    private Long idPayment;

    @Column("status")
    private String status;

    @Column("expires_at")
    private Instant expiresAt;

    @Builder.Default
    @Column("used_at")
    private Instant usedAt = Instant.now();
}

