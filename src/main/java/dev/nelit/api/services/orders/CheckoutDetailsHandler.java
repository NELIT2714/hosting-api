package dev.nelit.api.services.orders;

import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.request.checkout.CheckoutDetails;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.enums.PaymentGateway;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface CheckoutDetailsHandler<T extends CheckoutDetails> {
    Class<T> getSupportedType();
    Mono<Tuple2<PaymentResponse, CheckoutLineItem>> prepare(Long idUser, PaymentGateway gateway, T details);
}
