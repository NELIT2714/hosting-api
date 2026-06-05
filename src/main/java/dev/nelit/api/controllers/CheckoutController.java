package dev.nelit.api.controllers;

import dev.nelit.api.domain.exception.ipPool.NoAvailableAddressesException;
import dev.nelit.api.dto.request.checkout.CheckoutRequest;
import dev.nelit.api.dto.response.CheckoutResponse;
import dev.nelit.api.services.CheckoutService;
import dev.nelit.api.services.IpPoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final IpPoolService ipPoolService;

    @PostMapping
    public Mono<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                Long idUser = (Long) Objects.requireNonNull(ctx.getAuthentication()).getPrincipal();
                return ipPoolService.hasAvailable()
                    .flatMap(hasIp -> {
                        if (!hasIp) return Mono.error(new NoAvailableAddressesException());
                        return checkoutService.checkout(idUser, request);
                    });
            })
            .map(url -> new CheckoutResponse(url.url()));
    }

}
