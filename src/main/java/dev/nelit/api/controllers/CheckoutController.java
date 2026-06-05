package dev.nelit.api.controllers;

import dev.nelit.api.domain.exception.ipPool.NoAvailableAddressesException;
import dev.nelit.api.dto.request.checkout.CheckoutRequest;
import dev.nelit.api.dto.response.CheckoutResponse;
import dev.nelit.api.services.CheckoutService;
import dev.nelit.api.services.IpPoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Tag(name = "Checkout", description = "VPS order payment processing")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final IpPoolService ipPoolService;

    @Operation(
        summary = "Create a checkout session",
        description = "Initiates a Stripe payment session for the selected VPS plan. Returns a redirect URL to the payment page.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Checkout session created successfully",
                content = @Content(schema = @Schema(implementation = CheckoutResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503", description = "No available IP addresses in the pool",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
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
