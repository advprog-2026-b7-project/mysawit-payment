package id.ac.ui.cs.advprog.mysawit.payment.exception;

import org.springframework.http.HttpStatus;

public class PayrollApiException extends RuntimeException {
    private final PayrollErrorKey errorKey;
    private final HttpStatus status;

    public PayrollApiException(PayrollErrorKey errorKey, HttpStatus status, String message) {
        super(message);
        this.errorKey = errorKey;
        this.status = status;
    }

    public PayrollErrorKey getErrorKey() {
        return errorKey;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
