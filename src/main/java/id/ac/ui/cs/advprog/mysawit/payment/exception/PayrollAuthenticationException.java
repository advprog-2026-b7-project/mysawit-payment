package id.ac.ui.cs.advprog.mysawit.payment.exception;

import org.springframework.http.HttpStatus;

public class PayrollAuthenticationException extends PayrollApiException {
    public PayrollAuthenticationException(String message) {
        super(PayrollErrorKey.UNAUTHORIZED, HttpStatus.UNAUTHORIZED, message);
    }
}
