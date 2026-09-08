package dev.nelit.api.services.impl.payments.stripe;

import com.stripe.model.Coupon;
import com.stripe.model.checkout.Session;
import com.stripe.param.CouponCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import dev.nelit.api.dto.CheckoutLineItem;
import dev.nelit.api.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class StripeCheckoutServiceImpl {

    public Mono<String> createSession(PaymentResponse payment, CheckoutLineItem lineItem, Integer discountPercent) {
        return Mono.fromCallable(() -> {
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
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
                        .setUnitAmountDecimal(lineItem.fullPrice().movePointRight(2))  // всегда полная цена, без реконструкции
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(lineItem.description())
                            .build())
                        .build())
                    .build());

            if (discountPercent != null && discountPercent > 0) {
                builder.addDiscount(
                    SessionCreateParams.Discount.builder()
                        .setCoupon(createCoupon(discountPercent))
                        .build());
            }

            return Session.create(builder.build()).getUrl();
        });
    }

    private String createCoupon(int discountPercent) throws com.stripe.exception.StripeException {
        CouponCreateParams couponParams = CouponCreateParams.builder()
            .setPercentOff(BigDecimal.valueOf(discountPercent))
            .setDuration(CouponCreateParams.Duration.ONCE)
            .build();
        return Coupon.create(couponParams).getId();
    }
}
