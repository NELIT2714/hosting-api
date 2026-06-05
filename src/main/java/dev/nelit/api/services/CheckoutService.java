package dev.nelit.api.services;

import dev.nelit.api.dto.request.checkout.CheckoutRequest;
import dev.nelit.api.dto.response.CheckoutResponse;
import reactor.core.publisher.Mono;

public interface CheckoutService {
    Mono<CheckoutResponse> checkout(Long idUser, CheckoutRequest request);
}
