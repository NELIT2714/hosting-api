package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.Payment;
import dev.nelit.api.enums.PaymentGateway;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<Payment, Long> {
    Mono<Payment> findByGatewayAndGatewayPaymentId(PaymentGateway gateway, String gatewayPaymentId);
}
