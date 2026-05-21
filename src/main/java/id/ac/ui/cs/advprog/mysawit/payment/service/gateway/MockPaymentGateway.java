package id.ac.ui.cs.advprog.mysawit.payment.service.gateway;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MockPaymentGateway implements PaymentGateway {

    private static final Logger logger =
            LoggerFactory.getLogger(MockPaymentGateway.class);

    @Override
    public String createInvoice(Double amount,
                                String customerName,
                                String externalId) {

        logger.info(
                "Mock Gateway: create invoice amount={} customer={} externalId={}",
                amount, customerName, externalId
        );

        // fake payment URL
        return "https://mock-payment.local/invoice/" + externalId;
    }

    @Override
    public boolean verifyCallback(String xenditCallbackToken,
                                  String status) {

        logger.info(
                "Mock Gateway: verify callback token={} status={}",
                xenditCallbackToken, status
        );

        return true;
    }
}