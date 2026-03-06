package id.ac.ui.cs.advprog.mysawit.payment.service.gateway;

public interface PaymentGateway {
    boolean processPayment(Double amount, String destinationAccount);
}