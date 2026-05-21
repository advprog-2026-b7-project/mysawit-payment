package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.model.Wallet;
import id.ac.ui.cs.advprog.mysawit.payment.repository.WalletRepository;
import id.ac.ui.cs.advprog.mysawit.payment.service.gateway.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);
    private static final double RUPIAH_PER_SAWIT_DOLLAR = 10_000.0;

    private final WalletRepository walletRepository;
    private final PaymentGateway paymentGateway;

    public WalletServiceImpl(WalletRepository walletRepository, PaymentGateway paymentGateway) {
        this.walletRepository = walletRepository;
        this.paymentGateway   = paymentGateway;
    }

    @Override
    @Transactional
    public String createTopUpPaymentLink(String adminId, String adminName, Double amountSawitDollar) {
        double amountRupiah = amountSawitDollar * RUPIAH_PER_SAWIT_DOLLAR;

        String externalId = "TOPUP-" + adminId + "-" + UUID.randomUUID()
                + "-SD" + amountSawitDollar.intValue();

        String paymentUrl = paymentGateway.createInvoice(amountRupiah, adminName, externalId);

        if (paymentUrl != null) {
            logger.info("Payment link dibuat untuk adminId={}, amount={}SD, externalId={}",
                    adminId, amountSawitDollar, externalId);
        } else {
            logger.error("Gagal membuat payment link untuk adminId={}", adminId);
        }

        return paymentUrl;
    }

    @Override
    @Transactional
    public boolean handleXenditCallback(String callbackToken, String status,
                                        String externalId, Object amountRupiahObj) {
        if (!paymentGateway.verifyCallback(callbackToken, status)) {
            return false;
        }
        if (externalId == null || !externalId.startsWith("TOPUP-")) {
            logger.warn("externalId tidak valid: {}", externalId);
            return false;
        }

        try {
            int sdIndex = externalId.lastIndexOf("-SD");
            double amountSawitDollar = Double.parseDouble(externalId.substring(sdIndex + 3));

            String withoutPrefix = externalId.substring("TOPUP-".length());
            String adminId = withoutPrefix.substring(0, withoutPrefix.indexOf("-"));

            creditWallet(adminId, amountSawitDollar);

            logger.info("Wallet adminId={} dikreditkan {}SD setelah pembayaran Xendit berhasil",
                    adminId, amountSawitDollar);
            return true;

        } catch (Exception e) {
            logger.error("Error parsing externalId saat callback: {}, error: {}", externalId, e.getMessage());
            return false;
        }
    }

    @Override
    public Double getBalance(String userId) {
        return walletRepository.findByUserId(userId)
                .map(Wallet::getBalance)
                .orElse(0.0);
    }

    @Override
    @Transactional
    public void transferForPayroll(String adminId, String workerId, Double amountSawitDollar) {
        Wallet adminWallet  = getOrCreateWallet(adminId);
        Wallet workerWallet = getOrCreateWallet(workerId);

        if (adminWallet.getBalance() < amountSawitDollar) {
            throw new IllegalStateException(
                    "Saldo Admin tidak cukup untuk membayar payroll. " +
                            "Saldo: " + adminWallet.getBalance() + "SD, " +
                            "Dibutuhkan: " + amountSawitDollar + "SD");
        }

        adminWallet.setBalance(adminWallet.getBalance() - amountSawitDollar);
        workerWallet.setBalance(workerWallet.getBalance() + amountSawitDollar);

        walletRepository.save(adminWallet);
        walletRepository.save(workerWallet);

        logger.info("Transfer {}SD dari admin={} ke worker={}", amountSawitDollar, adminId, workerId);
    }

    private void creditWallet(String userId, Double amount) {
        Wallet wallet = getOrCreateWallet(userId);
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);
    }

    private Wallet getOrCreateWallet(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUserId(userId);
                    w.setBalance(0.0);
                    return walletRepository.save(w);
                });
    }
}