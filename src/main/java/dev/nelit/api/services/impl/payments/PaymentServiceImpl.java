package dev.nelit.api.services.impl.payments;

import dev.nelit.api.domain.entity.Payment;
import dev.nelit.api.domain.exception.payment.DuplicatePaymentException;
import dev.nelit.api.domain.exception.payment.PaymentNotFoundException;
import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.enums.PaymentType;
import dev.nelit.api.repository.PaymentRepository;
import dev.nelit.api.services.payments.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Mono<Payment> create(Long idUser, PaymentStatus status, PaymentGateway gateway, String gatewayPaymentId, BigDecimal amount, String currency, PaymentType type) {
        return paymentRepository.save(Payment.builder()
                .idUser(idUser)
                .gateway(gateway)
                .gatewayPaymentId(gatewayPaymentId)
                .amount(amount)
                .currency(currency)
                .status(status)
                .type(type)
                .build())
            .onErrorMap(DuplicateKeyException.class, _ -> new DuplicatePaymentException());
    }

    @Override
    public Mono<Payment> update(Long idPayment, PaymentStatus status, String gatewayPaymentId) {
        return paymentRepository.findById(idPayment)
            .switchIfEmpty(Mono.error(new PaymentNotFoundException()))
            .flatMap(payment -> {
                payment.setStatus(status);
                payment.setGatewayPaymentId(gatewayPaymentId);
                return paymentRepository.save(payment);
            });
    }
}
