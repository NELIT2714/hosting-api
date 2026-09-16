package dev.nelit.api.domain.exception.nowpayments;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidNowPaymentsSignatureException extends DomainException {
    public InvalidNowPaymentsSignatureException() {
        super("NOW_PAYMENTS_INVALID_IPN_SIGNATURE", "NowPayments ipn signature is invalid", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
