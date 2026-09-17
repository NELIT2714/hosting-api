package dev.nelit.api.services.impl.payment.nowpayments;

import dev.nelit.api.config.NowPaymentsProperties;
import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.nowpayments.NowPaymentsInvoiceRequest;
import dev.nelit.api.dto.nowpayments.NowPaymentsInvoiceResponse;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.services.payment.nowpayments.NowPaymentsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NowPaymentsCheckoutServiceImpl {

    private final NowPaymentsClient nowPaymentsClient;
    private final NowPaymentsProperties properties;

    public Mono<String> createSession(PaymentResponse payment, CheckoutLineItem lineItem, Integer discountPercent) {
        NowPaymentsInvoiceRequest request = new NowPaymentsInvoiceRequest(
            lineItem.unitAmount(),
            lineItem.currency().toLowerCase(),
            String.valueOf(payment.idPayment()),
            lineItem.description(),
            properties.getIpnCallbackUrl(),
            properties.getSuccessUrl(),
            properties.getCancelUrl()
        );

        return nowPaymentsClient.createInvoice(request).map(NowPaymentsInvoiceResponse::invoiceUrl);
    }
}
