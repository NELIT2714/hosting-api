package dev.nelit.api.services.impl.orders;

import dev.nelit.api.domain.entity.Payment;
import dev.nelit.api.enums.PaymentType;
import dev.nelit.api.services.payments.PaymentFulfillmentHandler;
import dev.nelit.api.services.orders.VpsOrderService;
import dev.nelit.api.services.vm.VmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class VpsPurchaseFulfillmentHandler implements PaymentFulfillmentHandler {

    private final VpsOrderService vpsOrderService;
    private final VmService vmService;

    @Override
    public PaymentType getSupportedType() {
        return PaymentType.VPS_PURCHASE;
    }

    @Override
    public Mono<Void> fulfill(Payment payment) {
        return vpsOrderService.getByIdPayment(payment.getIdPayment())
            .flatMap(order -> vmService.create(payment.getIdUser(), order.getIdPlan(), order.getIdOsImage())
                .flatMap(vm -> vpsOrderService.setVm(order.getIdOrder(), vm.getIdVM())))
            .then();
    }
}
