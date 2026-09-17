package dev.nelit.api.services.impl.payment.order;

import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.enums.PaymentType;
import dev.nelit.api.services.payment.PaymentFulfillmentHandler;
import dev.nelit.api.services.payment.order.VpsRenewalOrderService;
import dev.nelit.api.services.vm.VmLifecycleService;
import dev.nelit.api.services.vm.VmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class VpsRenewalFulfillmentHandler implements PaymentFulfillmentHandler {

    private final VpsRenewalOrderService vpsRenewalOrderService;
    private final VmService vmService;
    private final VmLifecycleService vmLifecycleService;

    @Override
    public PaymentType getSupportedType() {
        return PaymentType.VPS_RENEWAL;
    }

    @Override
    public Mono<Void> fulfill(PaymentResponse payment) {
        return vpsRenewalOrderService.getByIdPayment(payment.idPayment())
            .flatMap(order -> vmService.renew(order.getIdVm(), order.getDays())
                .then(vmLifecycleService.unblockIfBlocked(order.getIdVm())))
            .then();
    }
}
