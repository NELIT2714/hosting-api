package dev.nelit.api.services.impl.orders;

import dev.nelit.api.domain.entity.VpsOrder;
import dev.nelit.api.domain.exception.payment.PaymentNotFoundException;
import dev.nelit.api.repository.VpsOrderRepository;
import dev.nelit.api.services.orders.VpsOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VpsOrderServiceImpl implements VpsOrderService {

    private final VpsOrderRepository vpsOrderRepository;

    @Override
    public Mono<VpsOrder> create(Long idPayment, Long idPlan, Long idOsImage) {
        return vpsOrderRepository.save(VpsOrder.builder()
            .idPayment(idPayment)
            .idPlan(idPlan)
            .idOsImage(idOsImage)
            .build());
    }

    @Override
    public Mono<VpsOrder> getByIdPayment(Long idPayment) {
        return vpsOrderRepository.findByIdPayment(idPayment)
            .switchIfEmpty(Mono.error(new PaymentNotFoundException()));
    }
}
