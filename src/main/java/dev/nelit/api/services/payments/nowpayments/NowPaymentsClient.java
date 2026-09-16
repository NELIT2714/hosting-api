package dev.nelit.api.services.payments.nowpayments;

import dev.nelit.api.dto.nowpayments.NowPaymentsInvoiceRequest;
import dev.nelit.api.dto.nowpayments.NowPaymentsInvoiceResponse;
import reactor.core.publisher.Mono;

public interface NowPaymentsClient {
    Mono<NowPaymentsInvoiceResponse> createInvoice(NowPaymentsInvoiceRequest request);
}
