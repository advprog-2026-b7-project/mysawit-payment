package id.ac.ui.cs.advprog.mysawit.payment.service.gateway;

public interface PaymentGateway {
    String createInvoice(Double amount, String customerName, String externalId);
    boolean verifyCallback(String xenditCallbackToken, String status);
}