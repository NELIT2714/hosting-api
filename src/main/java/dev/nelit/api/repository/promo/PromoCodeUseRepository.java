package dev.nelit.api.repository.promo;

import dev.nelit.api.domain.entity.promo.PromoCodeUse;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PromoCodeUseRepository extends ReactiveCrudRepository<PromoCodeUse, Long> {

    @Query("""
        INSERT INTO promo_codes_uses (id_user, id_promo, id_payment, status, expires_at)
        VALUES (:userId, :idPromo, :idPayment, 'RESERVED', now() + interval '30 minutes')
        """)
    Mono<Void> reserve(Long userId, Long idPromo, Long idPayment);

    @Modifying
    @Query("""
        UPDATE promo_codes_uses SET status = 'CONFIRMED', expires_at = NULL
        WHERE id_payment = :idPayment AND status = 'RESERVED'
        """)
    Mono<Integer> confirmByPayment(Long idPayment);

    @Modifying
    @Query("""
        UPDATE promo_codes_uses SET status = 'CANCELLED'
        WHERE id_payment = :idPayment AND status = 'RESERVED'
        """)
    Mono<Integer> cancelByPayment(Long idPayment);

    @Modifying
    @Query("""
        UPDATE promo_codes_uses SET status = 'EXPIRED'
        WHERE status = 'RESERVED' AND expires_at <= now()
        """)
    Mono<Integer> releaseExpired();
}
