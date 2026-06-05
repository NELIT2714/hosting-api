package dev.nelit.api.services.impl;

import dev.nelit.api.domain.exception.payment.GatewayNotImplemented;
import dev.nelit.api.dto.request.checkout.CheckoutRequest;
import dev.nelit.api.dto.request.checkout.VpsCheckoutDetails;
import dev.nelit.api.dto.response.CheckoutResponse;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.services.CheckoutService;
import dev.nelit.api.services.PlanService;
import dev.nelit.api.services.impl.payments.stripe.StripeCheckoutServiceImpl;
import dev.nelit.api.services.orders.VpsOrderService;
import dev.nelit.api.services.payments.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final PaymentService paymentService;
    private final VpsOrderService vpsOrderService;
    private final PlanService planService;
    private final StripeCheckoutServiceImpl stripeCheckoutService;

    @Override
    public Mono<CheckoutResponse> checkout(Long idUser, CheckoutRequest request) {
        return switch (request.details()) {
            case VpsCheckoutDetails vps -> planService.getById(vps.idPlan())
                .flatMap(plan -> paymentService.create(idUser, PaymentStatus.PENDING, request.gateway(), null, plan.pricePerMonth(), "USD", request.details().type()))
                .flatMap(payment -> vpsOrderService.create(payment.getIdPayment(), vps.idPlan(), vps.idOsImage())
                    .thenReturn(payment))
                .flatMap(payment -> switch (request.gateway()) {
                    case STRIPE -> stripeCheckoutService.createSession(idUser, payment.getIdPayment(), payment.getAmount(), "USD").map(CheckoutResponse::new);
                    default -> Mono.error(new GatewayNotImplemented());
                });
        };
    }
}
