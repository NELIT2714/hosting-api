package dev.nelit.api.domain.exception.payment;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PaymentFulfillmentNotSupportedException extends DomainException {
    public PaymentFulfillmentNotSupportedException() {
        super("PAYMENT_FULFILLMENT_NOT_SUPPORTED", "No fulfillment handler for this payment type", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
