package dev.nelit.api.services.impl;

import dev.nelit.api.domain.exception.payment.GatewayNotImplemented;
import dev.nelit.api.dto.request.checkout.CheckoutRequest;
import dev.nelit.api.dto.request.checkout.VpsCheckoutDetails;
import dev.nelit.api.dto.request.checkout.VpsRenewalDetails;
import dev.nelit.api.dto.response.CheckoutResponse;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.dto.response.PlanResponse;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.mappers.PaymentMapper;
import dev.nelit.api.services.CheckoutService;
import dev.nelit.api.services.OsImageService;
import dev.nelit.api.services.PlanService;
import dev.nelit.api.services.impl.orders.VpsRenewalOrderServiceImpl;
import dev.nelit.api.services.impl.payments.stripe.StripeCheckoutServiceImpl;
import dev.nelit.api.services.impl.vm.VmServiceImpl;
import dev.nelit.api.services.orders.VpsOrderService;
import dev.nelit.api.services.payments.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final PaymentService paymentService;
    private final VpsOrderService vpsOrderService;
    private final VpsRenewalOrderServiceImpl vpsRenewalOrderService;
    private final PlanService planService;
    private final OsImageService osImageService;
    private final StripeCheckoutServiceImpl stripeCheckoutService;
    private final PaymentMapper paymentMapper;
    private final VmServiceImpl vmService;
    
    private final String CURRENCY = "USD";

    @Override
    public Mono<CheckoutResponse> checkout(Long idUser, CheckoutRequest request) {
        return preparePayment(idUser, request)
            .flatMap(tuple -> switch (request.gateway()) {
                case STRIPE -> stripeCheckoutService.createSession(tuple.getT1(), tuple.getT2()).map(CheckoutResponse::new);
                default -> Mono.error(new GatewayNotImplemented());
            });
    }

    private Mono<Tuple2<PaymentResponse, PlanResponse>> preparePayment(Long idUser, CheckoutRequest request) {
        return switch (request.details()) {
            case VpsCheckoutDetails vps -> planService.getById(vps.idPlan())
                .flatMap(plan -> paymentService.create(idUser, PaymentStatus.PENDING, request.gateway(), null, plan.pricePerMonth(), CURRENCY, request.details().type())
                    .map(paymentMapper::toResponse)
                    .flatMap(payment -> osImageService.getById(vps.idOsImage())
                        .flatMap(image -> vpsOrderService.create(payment.idPayment(), vps.idPlan(), image.idOsImage())
                            .thenReturn(payment))
                    )
                    .map(payment -> Tuples.of(payment, plan))
                );
            case VpsRenewalDetails renewal -> vmService.getById(renewal.idVm())
                .flatMap(vm -> planService.getById(vm.plan().idPlan())
                    .flatMap(plan -> paymentService.create(idUser, PaymentStatus.PENDING, request.gateway(), null, plan.pricePerMonth(), CURRENCY, request.details().type())
                        .flatMap(payment -> vpsRenewalOrderService.create(payment.getIdPayment(), renewal.idVm(), 30)
                            .thenReturn(paymentMapper.toResponse(payment)))
                        .map(payment -> Tuples.of(payment, plan))
                    )
                );
        };
    }
}
