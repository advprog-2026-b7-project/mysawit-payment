package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.dto.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.payment.dto.TopUpRequest;
import id.ac.ui.cs.advprog.mysawit.payment.security.PayrollJwtClaimsResolver;
import id.ac.ui.cs.advprog.mysawit.payment.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    private final WalletService walletService;
    private final PayrollJwtClaimsResolver claimsResolver;

    public WalletController(WalletService walletService,
                            PayrollJwtClaimsResolver claimsResolver) {
        this.walletService  = walletService;
        this.claimsResolver = claimsResolver;
    }

    @PostMapping("/topup")
    public ResponseEntity<ApiSuccessResponse<Map<String, String>>> topUp(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody TopUpRequest request) {

        // Hanya Admin Utama yang bisa top-up
        var claims   = claimsResolver.resolveApprover(authorization);
        String adminId   = claims.userId();   // record accessor
        String adminName = claims.name();     // record accessor

        String paymentUrl = walletService.createTopUpPaymentLink(
                adminId, adminName, request.getAmountSawitDollar());

        if (paymentUrl == null) {
            return ResponseEntity.internalServerError()
                    .body(new ApiSuccessResponse<>(null));
        }

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(Map.of("paymentUrl", paymentUrl)));
    }

    @PostMapping("/xendit/callback")
    public ResponseEntity<String> xenditCallback(
            @RequestHeader("x-callback-token") String callbackToken,
            @RequestBody Map<String, Object> payload) {

        String status     = (String) payload.get("status");
        String externalId = (String) payload.get("external_id");
        Object amountObj  = payload.get("amount");

        logger.info("Xendit callback diterima: externalId={}, status={}", externalId, status);

        boolean valid = walletService.handleXenditCallback(
                callbackToken, status, externalId, amountObj);

        if (!valid) {
            logger.warn("Xendit callback ditolak: externalId={}", externalId);
            return ResponseEntity.badRequest().body("Invalid callback");
        }
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiSuccessResponse<Map<String, Object>>> getBalance(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        var claims  = claimsResolver.resolve(authorization);
        String userId = claims.workerId();   // record accessor

        Double balance = walletService.getBalance(userId);

        return ResponseEntity.ok(new ApiSuccessResponse<>(
                Map.of("userId", userId, "balance", balance, "currency", "SawitDollar")));
    }
}