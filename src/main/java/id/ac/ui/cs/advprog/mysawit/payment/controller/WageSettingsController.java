package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.dto.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.payment.dto.WageSettingsRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.WageSettingsResponse;
import id.ac.ui.cs.advprog.mysawit.payment.exception.PayrollAuthorizationException;
import id.ac.ui.cs.advprog.mysawit.payment.service.WageSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/payment/wage-settings")
@CrossOrigin(origins = "http://localhost:3000")
public class WageSettingsController {

    @Autowired
    private WageSettingsService wageSettingsService;

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<WageSettingsResponse>> getWageSettings() {
        WageSettingsResponse settings = wageSettingsService.getWageSettings();
        return ResponseEntity.ok(new ApiSuccessResponse<>(settings));
    }

    @PatchMapping
    public ResponseEntity<ApiSuccessResponse<WageSettingsResponse>> updateWageSettings(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody WageSettingsRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !hasRole(auth, "ADMIN")) {
            throw new PayrollAuthorizationException("Only ADMIN can update wage settings");
        }
        WageSettingsResponse updated = wageSettingsService.updateWageSettings(request);
        return ResponseEntity.ok(new ApiSuccessResponse<>(updated));
    }

    private boolean hasRole(Authentication auth, String role) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
