package dev.nelit.api.services.impl.payments.stripe;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class StripeCheckoutServiceImpl {

    public Mono<String> createSession(PaymentResponse payment, CheckoutLineItem lineItem) {
        return Mono.fromCallable(() -> {
            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payment/success")
                .setCancelUrl("http://localhost:3000/payment/cancel")
                .setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES).getEpochSecond())
                .putMetadata("payment_id", String.valueOf(payment.idPayment()))
                .setPaymentIntentData(
                    SessionCreateParams.PaymentIntentData.builder()
                        .putMetadata("payment_id", String.valueOf(payment.idPayment()))
                        .build())
                .addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity(lineItem.quantity())
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(lineItem.currency().toLowerCase())
                        .setUnitAmountDecimal(lineItem.unitAmount().movePointRight(2))
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(lineItem.description())
                            .build())
                        .build())
                    .build())
                .build();

            return Session.create(params).getUrl();
        });
    }
}
