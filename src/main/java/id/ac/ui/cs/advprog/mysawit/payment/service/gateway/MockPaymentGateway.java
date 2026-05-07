package id.ac.ui.cs.advprog.mysawit.payment.service.gateway;

import org.springframework.stereotype.Service;

@Service
public class MockPaymentGateway implements PaymentGateway {
    @Override
    public boolean processPayment(Double amount, String destinationAccount) {
        String message = ">>> Mock Gateway: Processing Rp" + amount + " to "
                + destinationAccount;
        System.out.println(message);
        return true;
    }
}