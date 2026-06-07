package dev.nelit.api.services.impl.payments.stripe;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import dev.nelit.api.domain.entity.Payment;
import dev.nelit.api.domain.entity.Plan;
import dev.nelit.api.dto.response.PaymentResponse;
import dev.nelit.api.dto.response.PlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class StripeCheckoutServiceImpl {

    public Mono<String> createSession(PaymentResponse payment, PlanResponse plan) {
        return Mono.fromCallable(() -> {
            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payment/success")
                .setCancelUrl("http://localhost:3000/payment/cancel")
                .setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES).getEpochSecond())
                .putMetadata("user_id", String.valueOf(payment.idUser()))
                .putMetadata("payment_id", String.valueOf(payment.idPayment()))
                .putMetadata("type", payment.type().name())
                .addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(payment.currency().toLowerCase())
                        .setUnitAmountDecimal(payment.amount().movePointRight(2))
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(
                                String.format("VPS %s — %d vCPU, %d GB RAM, %d GB SSD, 1 Gbps",
                                plan.planName(), plan.vcpus(), plan.ramMb() / 1024, plan.diskGb())
                            )
                            .build())
                        .build())
                    .build())
                .build();

            return Session.create(params).getUrl();
        });
    }
}
