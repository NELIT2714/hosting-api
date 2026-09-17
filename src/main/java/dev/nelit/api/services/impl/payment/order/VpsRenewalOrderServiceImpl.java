package dev.nelit.api.services.impl.payment.order;

import dev.nelit.api.domain.entity.vps.VpsRenewalOrder;
import dev.nelit.api.domain.exception.payment.PaymentNotFoundException;
import dev.nelit.api.repository.VpsRenewalOrderRepository;
import dev.nelit.api.services.payment.order.VpsRenewalOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VpsRenewalOrderServiceImpl implements VpsRenewalOrderService {

    private final VpsRenewalOrderRepository vpsRenewalOrderRepository;

    @Override
    public Mono<VpsRenewalOrder> create(Long idPayment, Long idVm, Integer days) {
        return vpsRenewalOrderRepository.save(VpsRenewalOrder.builder()
            .idPayment(idPayment)
            .idVm(idVm)
            .days(days)
            .build());
    }

    @Override
    public Mono<VpsRenewalOrder> getByIdPayment(Long idPayment) {
        return vpsRenewalOrderRepository.findByIdPayment(idPayment)
            .switchIfEmpty(Mono.error(new PaymentNotFoundException()));
    }
}
