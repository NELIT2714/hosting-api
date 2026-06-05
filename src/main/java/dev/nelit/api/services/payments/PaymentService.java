package dev.nelit.api.services.payments;

import dev.nelit.api.domain.entity.Payment;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.enums.PaymentType;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface PaymentService {
    Mono<Payment> create(Long idUser, PaymentStatus status, PaymentGateway gateway, String gatewayPaymentId, BigDecimal amount, String currency, PaymentType type);
    Mono<Payment> update(Long idPayment, PaymentStatus status, String gatewayPaymentId);
}
