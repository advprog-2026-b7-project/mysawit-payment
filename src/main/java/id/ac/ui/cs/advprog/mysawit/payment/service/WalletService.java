package id.ac.ui.cs.advprog.mysawit.payment.service;

public interface WalletService {

    String createTopUpPaymentLink(String adminId, String adminName, Double amountSawitDollar);

    boolean handleXenditCallback(String callbackToken, String status,
                                 String externalId, Object amountRupiah);

    Double getBalance(String userId);
    void transferForPayroll(String adminId, String workerId, Double amountSawitDollar);
}