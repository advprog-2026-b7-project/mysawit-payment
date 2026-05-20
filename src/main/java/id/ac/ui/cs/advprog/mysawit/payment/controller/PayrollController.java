package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.dto.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.payment.dto.PayrollApprovalRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.security.PayrollJwtClaimsResolver;
import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payroll")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST, RequestMethod.OPTIONS})
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private PayrollJwtClaimsResolver claimsResolver;

    @GetMapping("/list")
    public ResponseEntity<ApiSuccessResponse<List<Payroll>>> getPayrollList(
            @RequestParam(required = false) String tanggal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String workerId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        
        // Validate JWT token and user role
        claimsResolver.resolveViewer(authorization);
        
        LocalDate filterDate = (tanggal != null && !tanggal.isEmpty()) 
                ? LocalDate.parse(tanggal) 
                : null;

        List<Payroll> payrolls = payrollService.findPayrolls(filterDate, status, workerId);
        
        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }

    @GetMapping("/by-type/{payrollType}")
    public ResponseEntity<ApiSuccessResponse<List<Payroll>>>
            getPayrollByType(
                    @PathVariable String payrollType,
                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        // Validate JWT token
        claimsResolver.resolveViewer(authorization);
        
        List<Payroll> payrolls = payrollService.findByPayrollType(payrollType);
        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }

    @GetMapping("/by-worker/{workerId}")
    public ResponseEntity<ApiSuccessResponse<List<Payroll>>>
            getPayrollByWorkerId(
                    @PathVariable String workerId,
                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        // Validate JWT token
        claimsResolver.resolveViewer(authorization);
        
        List<Payroll> payrolls = payrollService.findByWorkerId(workerId);
        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiSuccessResponse<Payroll>> approvePayroll(
            @PathVariable UUID id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody PayrollApprovalRequest request) {
        // Validate JWT token and approver role (ADMIN only)
        claimsResolver.resolveApprover(authorization);

        Payroll payroll;
        if ("ACCEPT".equalsIgnoreCase(request.getAction())) {
            payroll = payrollService.acceptPayroll(id);
        } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
            if (request.getReason() == null || request.getReason().trim().isEmpty()) {
                throw new IllegalArgumentException("Rejection reason is required");
            }
            payroll = payrollService.rejectPayroll(id, request.getReason());
        } else {
            throw new IllegalArgumentException(
                    "Invalid action. Must be ACCEPT or REJECT");
        }

        return ResponseEntity.ok(new ApiSuccessResponse<>(payroll));
    }

    @PostMapping("/harvest/create")
    public ResponseEntity<ApiSuccessResponse<String>> createPayrollFromHarvest(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody HarvestPayrollRequest request) {
        // Validate JWT token (worker/buruh can create payroll)
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
        // Validate JWT token (worker/buruh can create payroll)
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