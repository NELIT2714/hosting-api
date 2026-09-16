package dev.nelit.api.domain.exception.nowpayments;

import dev.nelit.api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class NowPaymentsApiException extends DomainException {
    public NowPaymentsApiException() {
        super("NOW_PAYMENTS_API_ERROR", "NowPayments API error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
