package dev.nelit.api.services.impl.payment.order.balance;

import dev.nelit.api.domain.exception.payment.GatewayNotImplemented;
import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.request.checkout.BalanceTopUpDetails;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.services.payment.PaymentService;
import dev.nelit.api.services.payment.order.CheckoutDetailsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Component
@RequiredArgsConstructor
public class BalanceTopUpCheckoutHandler implements CheckoutDetailsHandler<BalanceTopUpDetails> {

    private static final String CURRENCY = "USD";

    private final PaymentService paymentService;

    @Override
    public Class<BalanceTopUpDetails> getSupportedType() {
        return BalanceTopUpDetails.class;
    }

    @Override
    public Mono<Tuple2<PaymentResponse, CheckoutLineItem>> prepare(Long idUser, PaymentGateway gateway, BalanceTopUpDetails details, int discountPercent) {
        if (gateway == PaymentGateway.BALANCE) throw new GatewayNotImplemented();

        return paymentService.create(idUser, PaymentStatus.PENDING, gateway, null, details.amount(), CURRENCY, details.type())
            .map(payment -> {
                CheckoutLineItem lineItem = new CheckoutLineItem(
                    "Balance top-up",
                    details.amount(), CURRENCY, 1L, details.amount());
                return Tuples.of(payment, lineItem);
            });
    }
}
