package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.dto.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.payment.dto.PayrollRejectRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.security.PayrollJwtClaimsResolver;
import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/payroll")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private PayrollJwtClaimsResolver claimsResolver;

    @GetMapping("/list")
    public ResponseEntity<ApiSuccessResponse<Page<Payroll>>> getPayrollList(
            @RequestParam(required = false) String tanggal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String workerId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PageableDefault(size = 20) Pageable pageable) {
        claimsResolver.resolveViewer(authorization);
        
        LocalDate filterDate = (tanggal != null && !tanggal.isEmpty()) 
                ? LocalDate.parse(tanggal) 
                : null;

        Page<Payroll> payrolls = payrollService.findPayrolls(
                filterDate, status, workerId, pageable);
        
        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }

    @GetMapping("/by-type/{payrollType}")
    public ResponseEntity<ApiSuccessResponse<Page<Payroll>>>
            getPayrollByType(
                    @PathVariable String payrollType,
                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                    @PageableDefault(size = 20) Pageable pageable) {
        claimsResolver.resolveViewer(authorization);
        
        Page<Payroll> payrolls = payrollService.findByPayrollType(payrollType, pageable);
        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }

    @GetMapping("/by-worker/{workerId}")
    public ResponseEntity<ApiSuccessResponse<Page<Payroll>>>
            getPayrollByWorkerId(
                    @PathVariable String workerId,
                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                    @PageableDefault(size = 20) Pageable pageable) {
        claimsResolver.resolveViewer(authorization);
        
        Page<Payroll> payrolls = payrollService.findByWorkerId(workerId, pageable);
        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiSuccessResponse<Payroll>> approvePayroll(
            @PathVariable UUID id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        claimsResolver.resolveApprover(authorization);

        Payroll payroll = payrollService.acceptPayroll(id);
        return ResponseEntity.ok(new ApiSuccessResponse<>(payroll));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiSuccessResponse<Payroll>> rejectPayroll(
            @PathVariable UUID id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody PayrollRejectRequest request) {
        claimsResolver.resolveApprover(authorization);

        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }

        Payroll payroll = payrollService.rejectPayroll(id, request.getReason());
        return ResponseEntity.ok(new ApiSuccessResponse<>(payroll));
    }

    @PostMapping("/harvest/create")
    public ResponseEntity<ApiSuccessResponse<String>> createPayrollFromHarvest(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody HarvestPayrollRequest request) {
        claimsResolver.resolve(authorization);
        
        payrollService.createPayrollFromHarvestApproval(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccessResponse<>(
                        "Payroll created successfully from harvest approval"));
    }

    @PostMapping("/delivery/create")
    public ResponseEntity<ApiSuccessResponse<String>> createPayrollFromDelivery(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody DeliveryPayrollRequest request) {
        claimsResolver.resolve(authorization);
        
        payrollService.createPayrollFromDeliveryApproval(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccessResponse<>(
                        "Payroll created successfully from delivery approval"));
    }
    @GetMapping("/{id}/status")
    public Map<String, String> getPayrollStatus(@PathVariable UUID id) {
        Payroll payroll = payrollService.findById(id);
        if (payroll == null) {
            return Map.of("error", "Payroll not found");
        }
        return Map.of(
                "id", payroll.getId().toString(),
                "status", payroll.getStatus()
        );
    }
}