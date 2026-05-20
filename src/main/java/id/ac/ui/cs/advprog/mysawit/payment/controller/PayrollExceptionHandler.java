package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.dto.PayrollErrorResponse;
import id.ac.ui.cs.advprog.mysawit.payment.exception.PayrollApiException;
import id.ac.ui.cs.advprog.mysawit.payment.exception.PayrollErrorKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class PayrollExceptionHandler {

    @ExceptionHandler(PayrollApiException.class)
    public ResponseEntity<PayrollErrorResponse> handlePayrollApiException(PayrollApiException ex) {
        PayrollErrorResponse response = new PayrollErrorResponse(
                "error",
                ex.getErrorKey(),
                ex.getMessage(),
                Instant.now());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        MissingServletRequestPartException.class,
        MissingRequestHeaderException.class
    })
    public ResponseEntity<PayrollErrorResponse> handleValidationException(Exception ex) {
        // Special handling for missing Authorization header
        if (ex instanceof MissingRequestHeaderException missingHeader) {
            if ("Authorization".equalsIgnoreCase(missingHeader.getHeaderName())) {
                PayrollErrorResponse response = new PayrollErrorResponse(
                        "error",
                        PayrollErrorKey.UNAUTHORIZED,
                        "Authorization header is required",
                        Instant.now());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }

        // Validation error handling
        List<String> errors = new ArrayList<>();
        if (ex instanceof MethodArgumentNotValidException validationEx) {
            validationEx.getBindingResult().getAllErrors().forEach(error ->
                    errors.add(error.getDefaultMessage()));
        }

        PayrollErrorResponse response = new PayrollErrorResponse(
                "error",
                PayrollErrorKey.INVALID_REQUEST,
                "Validation failed",
                errors,
                Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PayrollErrorResponse> handleGeneralException(Exception ex) {
        PayrollErrorResponse response = new PayrollErrorResponse(
                "error",
                PayrollErrorKey.INTERNAL_ERROR,
                "An unexpected error occurred",
                Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
