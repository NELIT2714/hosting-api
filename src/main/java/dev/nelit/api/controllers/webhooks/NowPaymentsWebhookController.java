package dev.nelit.api.controllers.webhooks;

import dev.nelit.api.services.payment.nowpayments.NowPaymentsWebhookService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/webhooks/nowpayments")
public class NowPaymentsWebhookController {

    private final NowPaymentsWebhookService nowPaymentsWebhookService;

    @PostMapping
    public Mono<Void> handleIpn(@RequestBody String rawBody,
                                @RequestHeader("x-nowpayments-sig") String signature) {
        log.info("NowPayments IPN raw body: {}", rawBody);
        log.info("NowPayments IPN signature header: {}", signature);
        return nowPaymentsWebhookService.handleIpn(rawBody, signature);
    }
}
