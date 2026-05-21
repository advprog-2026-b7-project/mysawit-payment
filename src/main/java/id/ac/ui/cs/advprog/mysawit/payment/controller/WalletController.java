package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.dto.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.payment.dto.TopUpRequest;
import id.ac.ui.cs.advprog.mysawit.payment.security.PayrollJwtClaimsResolver;
import id.ac.ui.cs.advprog.mysawit.payment.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class WalletController {

    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    private final WalletService walletService;
    private final PayrollJwtClaimsResolver claimsResolver;

    public WalletController(WalletService walletService,
                            PayrollJwtClaimsResolver claimsResolver) {
        this.walletService = walletService;
        this.claimsResolver = claimsResolver;
    }

    /**
     * Step 1 dari flow Xendit:
     * Frontend request ke sini → backend buat invoice di Xendit → return payment URL.
     *
     * POST /api/wallet/topup
     * Body: { "amountSawitDollar": 100 }   ← jumlah SawitDollar yang mau dibeli
     * Response: { "data": { "paymentUrl": "https://checkout.xendit.co/..." } }
     */
    @PostMapping("/topup")
    public ResponseEntity<ApiSuccessResponse<Map<String, String>>> topUp(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody TopUpRequest request) {

        // Hanya Admin Utama yang bisa top-up
        var claims = claimsResolver.resolveApprover(authorization);
        String adminId   = claims.getUserId();
        String adminName = claims.getUserName();

        String paymentUrl = walletService.createTopUpPaymentLink(
                adminId, adminName, request.getAmountSawitDollar());

        if (paymentUrl == null) {
            return ResponseEntity.internalServerError()
                    .body(new ApiSuccessResponse<>(null));
        }

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(Map.of("paymentUrl", paymentUrl)));
    }

    /**
     * Step 2 dari flow Xendit (callback/webhook):
     * Setelah user bayar, Xendit POST ke sini secara otomatis.
     * Backend verifikasi lalu tambah saldo wallet Admin.
     *
     * POST /api/wallet/xendit/callback
     * Header: x-callback-token (secret dari Xendit dashboard)
     * Body: { "external_id": "...", "status": "PAID", "amount": 1000000, ... }
     *
     * PENTING: endpoint ini harus EXEMPT dari JWT auth (tidak pakai Bearer token)
     * Tambahkan path ini ke SecurityConfig sebagai permitAll()
     */
    @PostMapping("/xendit/callback")
    public ResponseEntity<String> xenditCallback(
            @RequestHeader("x-callback-token") String callbackToken,
            @RequestBody Map<String, Object> payload) {

        String status     = (String) payload.get("status");
        String externalId = (String) payload.get("external_id");
        Object amountObj  = payload.get("amount");

        logger.info("Xendit callback diterima: externalId={}, status={}", externalId, status);

        boolean valid = walletService.handleXenditCallback(callbackToken, status, externalId, amountObj);

        if (!valid) {
            logger.warn("Xendit callback ditolak: externalId={}", externalId);
            return ResponseEntity.badRequest().body("Invalid callback");
        }

        // Xendit butuh response 200 agar tidak retry
        return ResponseEntity.ok("OK");
    }

    /**
     * GET saldo wallet user yang sedang login.
     * GET /api/wallet/balance
     */
    @GetMapping("/balance")
    public ResponseEntity<ApiSuccessResponse<Map<String, Object>>> getBalance(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        var claims  = claimsResolver.resolve(authorization);
        String userId = claims.getUserId();

        Double balance = walletService.getBalance(userId);

        return ResponseEntity.ok(new ApiSuccessResponse<>(
                Map.of("userId", userId, "balance", balance, "currency", "SawitDollar")));
    }
}