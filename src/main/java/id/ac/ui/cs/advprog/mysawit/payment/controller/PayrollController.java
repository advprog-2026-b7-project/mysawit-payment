package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.dto.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.payment.dto.PayrollRejectRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.exception.PayrollApiException;
import id.ac.ui.cs.advprog.mysawit.payment.exception.PayrollAuthorizationException;
import id.ac.ui.cs.advprog.mysawit.payment.exception.PayrollErrorKey;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.security.PayrollJwtClaimsResolver;
import id.ac.ui.cs.advprog.mysawit.payment.security.PayrollViewerContext;
import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.UUID;

@RestController
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
        
        LocalDate filterDate = null;
        if (tanggal != null && !tanggal.isEmpty()) {
            try {
                filterDate = LocalDate.parse(tanggal, DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                throw new PayrollApiException(PayrollErrorKey.INVALID_REQUEST,
                        HttpStatus.BAD_REQUEST,
                        "Invalid date format. Use yyyy-MM-dd");
            }
        }

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
        PayrollViewerContext viewer = claimsResolver.resolveViewer(authorization);
        String callerId = viewer.userId();
        String callerRole = viewer.role();
        String targetId = workerId;
        if (!"ADMIN".equals(callerRole) && !callerId.equals(targetId)) {
            throw new PayrollAuthorizationException("You can only view your own payroll");
        }
        
        Page<Payroll> payrolls = payrollService.findByWorkerId(targetId, pageable);
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
            throw new PayrollApiException(PayrollErrorKey.INVALID_REQUEST,
                    HttpStatus.BAD_REQUEST, "Rejection reason is required");
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
    public ResponseEntity<ApiSuccessResponse<Map<String, String>>> getPayrollStatus(
            @PathVariable UUID id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        claimsResolver.resolveViewer(authorization);
        Payroll payroll = payrollService.findById(id);
        if (payroll == null) {
            throw new PayrollApiException(PayrollErrorKey.NOT_FOUND,
                    HttpStatus.NOT_FOUND, "Payroll not found");
        }
        return ResponseEntity.ok(new ApiSuccessResponse<>(Map.of(
                "id", payroll.getId().toString(),
                "status", payroll.getStatus()
        )));
    }
}