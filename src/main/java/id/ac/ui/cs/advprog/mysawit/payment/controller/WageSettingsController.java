package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.dto.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.payment.dto.WageSettingsRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.WageSettingsResponse;
import id.ac.ui.cs.advprog.mysawit.payment.service.WageSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/wage-settings")
@Validated
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
            @Valid @RequestBody WageSettingsRequest request) {
        WageSettingsResponse updated = wageSettingsService.updateWageSettings(request);
        return ResponseEntity.ok(new ApiSuccessResponse<>(updated));
    }
}
