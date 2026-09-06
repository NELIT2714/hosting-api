package dev.nelit.api.repository.promo;

import dev.nelit.api.domain.entity.promo.PromoCode;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoCodeRepository extends ReactiveCrudRepository<PromoCode, Long> {
}
