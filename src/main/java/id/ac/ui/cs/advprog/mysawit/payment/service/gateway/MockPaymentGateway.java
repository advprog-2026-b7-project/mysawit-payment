package id.ac.ui.cs.advprog.mysawit.payment.service;

import org.springframework.stereotype.Service;

@Service
public class MockPaymentGateway implements PaymentGateway {
    @Override
    public boolean processPayment(Double amount, String destinationAccount) {
        System.out.println(">>> Mock Gateway: Processing Rp" + amount + " to " + destinationAccount);
        return true;
    }
}