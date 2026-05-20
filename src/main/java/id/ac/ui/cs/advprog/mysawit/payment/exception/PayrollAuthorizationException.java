package id.ac.ui.cs.advprog.mysawit.payment.exception;

import org.springframework.http.HttpStatus;

public class PayrollAuthorizationException extends PayrollApiException {
    public PayrollAuthorizationException(String message) {
        super(PayrollErrorKey.FORBIDDEN, HttpStatus.FORBIDDEN, message);
    }

    public PayrollAuthorizationException(PayrollErrorKey errorKey, String message) {
        super(errorKey, HttpStatus.FORBIDDEN, message);
    }
}
