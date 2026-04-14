package id.ac.ui.cs.advprog.mysawit.payment.service.gateway;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MockPaymentGateway implements PaymentGateway {
    private static final Logger logger = LoggerFactory.getLogger(MockPaymentGateway.class);

    @Override
    public boolean processPayment(Double amount, String destinationAccount) {
        logger.info("Mock Gateway: Processing Rp{} to {}", amount, destinationAccount);
        return true;
    }
}