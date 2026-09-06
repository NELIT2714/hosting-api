package dev.nelit.api.services.impl.orders;

import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.request.checkout.VpsCheckoutDetails;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.dto.response.PlanResponse;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.mappers.PaymentMapper;
import dev.nelit.api.services.OsImageService;
import dev.nelit.api.services.PlanService;
import dev.nelit.api.services.orders.CheckoutDetailsHandler;
import dev.nelit.api.services.orders.VpsOrderService;
import dev.nelit.api.services.payments.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Component
@RequiredArgsConstructor
public class VpsCheckoutHandler implements CheckoutDetailsHandler<VpsCheckoutDetails> {

    private static final String CURRENCY = "USD";

    private final PlanService planService;
    private final PaymentService paymentService;
    private final OsImageService osImageService;
    private final VpsOrderService vpsOrderService;
    private final PaymentMapper paymentMapper;

    @Override
    public Class<VpsCheckoutDetails> getSupportedType() {
        return VpsCheckoutDetails.class;
    }

    @Override
    public Mono<Tuple2<PaymentResponse, CheckoutLineItem>> prepare(Long idUser, PaymentGateway gateway, VpsCheckoutDetails details) {
        return planService.getById(details.idPlan())
            .flatMap(plan -> paymentService.create(idUser, PaymentStatus.PENDING, gateway, null, plan.pricePerMonth(), CURRENCY, details.type())
                .map(paymentMapper::toResponse)
                .flatMap(payment -> osImageService.getById(details.idOsImage())
                    .flatMap(image -> vpsOrderService.create(payment.idPayment(), details.idPlan(), image.idOsImage())
                        .thenReturn(payment)))
                .map(payment -> {
                    CheckoutLineItem lineItem = new CheckoutLineItem(
                        String.format("VPS %s — %d vCPU, %d GB RAM, %d GB SSD, 1 Gbps",
                            plan.planName(), plan.vcpus(), plan.ramMb() / 1024, plan.diskGb()),
                        plan.pricePerMonth(), CURRENCY, 1L);
                    return Tuples.of(payment, lineItem);
                }));
    }
}
