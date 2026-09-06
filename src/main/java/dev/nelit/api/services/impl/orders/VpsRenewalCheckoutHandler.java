package dev.nelit.api.services.impl.orders;

import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.request.checkout.VpsRenewalDetails;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.dto.response.PlanResponse;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.mappers.PaymentMapper;
import dev.nelit.api.services.PlanService;
import dev.nelit.api.services.impl.vm.VmServiceImpl;
import dev.nelit.api.services.orders.CheckoutDetailsHandler;
import dev.nelit.api.services.orders.VpsRenewalOrderService;
import dev.nelit.api.services.payments.PaymentService;
import dev.nelit.api.services.vm.VmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Component
@RequiredArgsConstructor
public class VpsRenewalCheckoutHandler implements CheckoutDetailsHandler<VpsRenewalDetails> {

    private static final String CURRENCY = "USD";
    private static final int RENEWAL_DAYS = 30;

    private final VmService vmService;
    private final PlanService planService;
    private final PaymentService paymentService;
    private final VpsRenewalOrderService vpsRenewalOrderService;
    private final PaymentMapper paymentMapper;

    @Override
    public Class<VpsRenewalDetails> getSupportedType() {
        return VpsRenewalDetails.class;
    }

    @Override
    public Mono<Tuple2<PaymentResponse, CheckoutLineItem>> prepare(Long idUser, PaymentGateway gateway, VpsRenewalDetails details) {
        return vmService.getById(details.idVm())
            .flatMap(vm -> planService.getById(vm.plan().idPlan())
                .flatMap(plan -> paymentService.create(idUser, PaymentStatus.PENDING, gateway, null, plan.pricePerMonth(), CURRENCY, details.type())
                    .flatMap(payment -> vpsRenewalOrderService.create(payment.getIdPayment(), details.idVm(), RENEWAL_DAYS)
                        .thenReturn(paymentMapper.toResponse(payment)))
                    .map(payment -> {
                        CheckoutLineItem lineItem = new CheckoutLineItem(
                            String.format("VPS renewal — %s, %d days", plan.planName(), RENEWAL_DAYS),
                            plan.pricePerMonth(), CURRENCY, 1L);
                        return Tuples.of(payment, lineItem);
                    })));
    }
}
