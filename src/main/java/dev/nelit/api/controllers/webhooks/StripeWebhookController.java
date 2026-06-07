package dev.nelit.api.controllers.webhooks;

import com.stripe.exception.SignatureVerificationException;
import dev.nelit.api.services.payments.stripe.StripeWebhookService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/webhooks/stripe")
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    @PostMapping
    public Mono<Void> handleWebhook(
        @RequestBody String rawBody,
        @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        return stripeWebhookService.handleEvent(rawBody, sigHeader)
            .thenReturn(ResponseEntity.ok().build())
            .onErrorResume(SignatureVerificationException.class, e ->
                Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build())
            ).then();
    }
}