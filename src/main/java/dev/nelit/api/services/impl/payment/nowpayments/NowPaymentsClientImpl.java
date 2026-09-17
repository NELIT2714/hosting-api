package dev.nelit.api.services.impl.payment.nowpayments;

import dev.nelit.api.domain.exception.nowpayments.NowPaymentsApiException;
import dev.nelit.api.dto.nowpayments.NowPaymentsInvoiceRequest;
import dev.nelit.api.dto.nowpayments.NowPaymentsInvoiceResponse;
import dev.nelit.api.services.payment.nowpayments.NowPaymentsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NowPaymentsClientImpl implements NowPaymentsClient {

    private final WebClient nowPaymentsWebClient;

    @Override
    public Mono<NowPaymentsInvoiceResponse> createInvoice(NowPaymentsInvoiceRequest request) {
        return nowPaymentsWebClient.post()
            .uri("/v1/invoice")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, resp -> resp.bodyToMono(String.class)
                .doOnNext(body -> log.error("NowPayments API error {}: {}", resp.statusCode().value(), body))
                .flatMap(body -> Mono.error(new NowPaymentsApiException())))
            .bodyToMono(NowPaymentsInvoiceResponse.class);
    }
}
