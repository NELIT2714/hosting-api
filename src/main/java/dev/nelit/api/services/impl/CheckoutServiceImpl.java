package dev.nelit.api.services.impl;

import dev.nelit.api.domain.exception.payment.CheckoutTypeNotSupportedException;
import dev.nelit.api.domain.exception.payment.GatewayNotImplemented;
import dev.nelit.api.dto.request.checkout.CheckoutDetails;
import dev.nelit.api.dto.request.checkout.CheckoutRequest;
import dev.nelit.api.dto.response.CheckoutResponse;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.services.CheckoutService;
import dev.nelit.api.services.orders.CheckoutDetailsHandler;
import dev.nelit.api.services.payments.PaymentGatewayStrategy;
import dev.nelit.api.services.promo.PromoCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final Map<PaymentGateway, PaymentGatewayStrategy> gatewayStrategies;
    private final Map<Class<? extends CheckoutDetails>, CheckoutDetailsHandler<CheckoutDetails>> detailsHandlers;
    private final PromoCodeService promoCodeService;

    @SuppressWarnings("unchecked")
    public CheckoutServiceImpl(List<PaymentGatewayStrategy> gatewayStrategies,
                               List<CheckoutDetailsHandler<?>> detailsHandlers,
                               PromoCodeService promoCodeService) {
        this.gatewayStrategies = gatewayStrategies.stream()
            .collect(Collectors.toMap(PaymentGatewayStrategy::getType, Function.identity()));
        this.detailsHandlers = detailsHandlers.stream()
            .collect(Collectors.toMap(CheckoutDetailsHandler::getSupportedType, h -> (CheckoutDetailsHandler<CheckoutDetails>) h));
        this.promoCodeService = promoCodeService;
    }

    @Override
    public Mono<CheckoutResponse> checkout(Long idUser, CheckoutRequest request) {
        CheckoutDetailsHandler<CheckoutDetails> handler = detailsHandlers.get(request.details().getClass());
        if (handler == null) return Mono.error(new CheckoutTypeNotSupportedException());

        PaymentGatewayStrategy strategy = gatewayStrategies.get(request.gateway());
        if (strategy == null) return Mono.error(new GatewayNotImplemented());

        String promoCode = request.promoCode();

        return peekDiscount(promoCode)
            .flatMap(discountPercent -> handler.prepare(idUser, request.gateway(), request.details(), discountPercent)
                .flatMap(tuple -> reservePromoIfPresent(promoCode, idUser, tuple.getT1().idPayment())
                    .thenReturn(tuple))
                .flatMap(tuple -> strategy.createSession(tuple.getT1(), tuple.getT2(), discountPercent)))
            .map(CheckoutResponse::new);
    }

    private Mono<Integer> peekDiscount(String promoCode) {
        if (promoCode == null || promoCode.isBlank()) {
            return Mono.just(0);
        }
        return promoCodeService.peekDiscount(promoCode);
    }

    private Mono<Void> reservePromoIfPresent(String promoCode, Long idUser, Long idPayment) {
        if (promoCode == null || promoCode.isBlank()) {
            return Mono.empty();
        }
        return promoCodeService.applyToPayment(promoCode, idUser, idPayment).then();
    }
}
