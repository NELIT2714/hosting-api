package dev.nelit.api.services.orders;

import dev.nelit.api.domain.entity.VpsOrder;
import reactor.core.publisher.Mono;

public interface VpsOrderService {
    Mono<VpsOrder> create(Long idPayment, Long idPlan, Long idOsImage);
    Mono<VpsOrder> getByIdPayment(Long idPayment);
}
