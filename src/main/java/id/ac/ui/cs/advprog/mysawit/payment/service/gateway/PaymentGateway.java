package id.ac.ui.cs.advprog.mysawit.payment.service;

public interface PaymentGateway {
    boolean processPayment(Double amount, String destinationAccount);
}