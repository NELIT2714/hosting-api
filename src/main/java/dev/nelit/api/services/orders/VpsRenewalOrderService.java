package dev.nelit.api.services.orders;

import dev.nelit.api.domain.entity.vps.VpsRenewalOrder;
import reactor.core.publisher.Mono;

public interface VpsRenewalOrderService {
    Mono<VpsRenewalOrder> create(Long idPayment, Long idVm, Integer days);
    Mono<VpsRenewalOrder> getByIdPayment(Long idPayment);
}
