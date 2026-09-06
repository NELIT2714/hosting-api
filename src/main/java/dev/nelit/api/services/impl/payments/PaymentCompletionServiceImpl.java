package dev.nelit.api.services.impl.payments;

import dev.nelit.api.domain.exception.payment.PaymentFulfillmentNotSupportedException;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.enums.PaymentType;
import dev.nelit.api.services.payments.PaymentCompletionService;
import dev.nelit.api.services.payments.PaymentFulfillmentHandler;
import dev.nelit.api.services.payments.PaymentService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentCompletionServiceImpl implements PaymentCompletionService {

    private final PaymentService paymentService;
    private final Map<PaymentType, PaymentFulfillmentHandler> fulfillmentHandlers;

    public PaymentCompletionServiceImpl(PaymentService paymentService, List<PaymentFulfillmentHandler> fulfillmentHandlers) {
        this.paymentService = paymentService;
        this.fulfillmentHandlers = fulfillmentHandlers.stream()
            .collect(Collectors.toMap(PaymentFulfillmentHandler::getSupportedType, Function.identity()));
    }

    @Override
    public Mono<Void> complete(Long idPayment, String gatewayPaymentId) {
        return paymentService.update(idPayment, PaymentStatus.SUCCEEDED, gatewayPaymentId)
            .flatMap(payment -> {
                PaymentFulfillmentHandler handler = fulfillmentHandlers.get(payment.getType());
                if (handler == null) return Mono.error(new PaymentFulfillmentNotSupportedException());
                return handler.fulfill(payment);
            });
    }

    @Override
    public Mono<Void> fail(Long idPayment, String gatewayPaymentId) {
        return paymentService.update(idPayment, PaymentStatus.FAILED, gatewayPaymentId).then();
    }
}
