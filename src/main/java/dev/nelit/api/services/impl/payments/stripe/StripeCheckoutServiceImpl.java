package dev.nelit.api.services.impl.payments.stripe;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StripeCheckoutServiceImpl {

    public Mono<String> createSession(Long idUser, Long idPayment, BigDecimal amount, String currency) {
        return Mono.fromCallable(() -> {
            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payment/success")
                .setCancelUrl("http://localhost:3000/payment/cancel")
                .putMetadata("user_id", String.valueOf(idUser))
                .putMetadata("payment_id", String.valueOf(idPayment))
                .addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(currency.toLowerCase())
                        .setUnitAmountDecimal(amount.movePointRight(2))
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName("VPS hosting")
                            .build())
                        .build())
                    .build())
                .build();

            return Session.create(params).getUrl();
        });
    }
}
