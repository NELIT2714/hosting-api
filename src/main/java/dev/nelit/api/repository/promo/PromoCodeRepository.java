package dev.nelit.api.repository.promo;

import dev.nelit.api.domain.entity.promo.PromoCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface PromoCodeRepository extends ReactiveCrudRepository<PromoCode, Long> {

    @Query("""
        SELECT * FROM promo_codes
        WHERE code = :code
          AND is_active = TRUE
          AND (expires_at IS NULL OR expires_at > now())
        FOR UPDATE
        """)
    Mono<PromoCode> findLockedByCode(String code);

    @Query("""
        SELECT COUNT(*) FROM promo_codes_uses
        WHERE id_promo = :idPromo
          AND (status = 'CONFIRMED' OR (status = 'RESERVED' AND expires_at > now()))
        """)
    Mono<Long> countActiveUses(Long idPromo);

    @Query("""
        SELECT EXISTS (
            SELECT 1 FROM promo_codes_uses
            WHERE id_promo = :idPromo AND id_user = :userId
              AND (status = 'CONFIRMED' OR (status = 'RESERVED' AND expires_at > now()))
        )
        """)
    Mono<Boolean> existsActiveUseByUser(Long idPromo, Long userId);

    @Query("""
    SELECT * FROM promo_codes
    WHERE code = :code
      AND is_active = TRUE
      AND (expires_at IS NULL OR expires_at > now())
    """)
    Mono<PromoCode> findActiveByCode(String code);
}
