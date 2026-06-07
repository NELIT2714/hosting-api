package dev.nelit.api.scheduler;

import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentExpirationScheduler {

    private final PaymentRepository paymentRepository;
    private final TransactionalOperator tx;

    @Scheduled(cron = "0 */35 * * * *", zone = "UTC")
    public void expireOldPayments() {
        Instant threshold = Instant.now().minus(35, ChronoUnit.MINUTES);

        paymentRepository.findAllByStatusAndCreatedAtBefore(PaymentStatus.PENDING, threshold)
            .flatMap(payment -> {
                payment.setStatus(PaymentStatus.FAILED);
                return paymentRepository.save(payment);
            })
            .as(tx::transactional)
            .doOnNext(payment -> log.debug("Payment {} marked as FAILED", payment.getIdPayment()))
            .doOnError(e -> log.error("Error expiring payments", e))
            .subscribe();
    }

}
