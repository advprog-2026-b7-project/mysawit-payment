package id.ac.ui.cs.advprog.mysawit.payment.dto;

import id.ac.ui.cs.advprog.mysawit.payment.exception.PayrollErrorKey;
import java.time.Instant;
import java.util.List;

public class PayrollErrorResponse {
    private String status;
    private PayrollErrorKey errorKey;
    private String message;
    private List<String> errors;
    private Instant timestamp;

    public PayrollErrorResponse(String status, PayrollErrorKey errorKey, String message) {
        this.status = status;
        this.errorKey = errorKey;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public PayrollErrorResponse(
            String status,
            PayrollErrorKey errorKey,
            String message,
            Instant timestamp) {
        this.status = status;
        this.errorKey = errorKey;
        this.message = message;
        this.timestamp = timestamp;
    }

    public PayrollErrorResponse(
            String status,
            PayrollErrorKey errorKey,
            String message,
            List<String> errors,
            Instant timestamp) {
        this.status = status;
        this.errorKey = errorKey;
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PayrollErrorKey getErrorKey() {
        return errorKey;
    }

    public void setErrorKey(PayrollErrorKey errorKey) {
        this.errorKey = errorKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
