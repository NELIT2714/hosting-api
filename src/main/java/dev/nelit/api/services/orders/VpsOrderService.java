package dev.nelit.api.services.orders;

import dev.nelit.api.domain.entity.vps.VpsOrder;
import reactor.core.publisher.Mono;

public interface VpsOrderService {
    Mono<VpsOrder> create(Long idPayment, Long idPlan, Long idOsImage);
    Mono<VpsOrder> setVm(Long idOrder, Long idVm);
    Mono<VpsOrder> getByIdPayment(Long idPayment);
    Mono<VpsOrder> getByIdVm(Long idVm);
}
