package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.dto.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.payment.dto.PayrollApprovalRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payroll")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST, RequestMethod.OPTIONS})
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @GetMapping("/list")
    public ResponseEntity<ApiSuccessResponse<List<Payroll>>> getPayrollList(
            @RequestParam(required = false) String tanggal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String workerId) {
        
        LocalDate filterDate = (tanggal != null && !tanggal.isEmpty()) 
                ? LocalDate.parse(tanggal) 
                : null;

        List<Payroll> payrolls = payrollService.findPayrolls(filterDate, status, workerId);
        
        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }

    @GetMapping("/by-type/{payrollType}")
    public ResponseEntity<ApiSuccessResponse<List<Payroll>>>
            getPayrollByType(@PathVariable String payrollType) {
        List<Payroll> payrolls = payrollService.findByPayrollType(payrollType);
        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }

    @GetMapping("/by-worker/{workerId}")
    public ResponseEntity<ApiSuccessResponse<List<Payroll>>>
            getPayrollByWorkerId(@PathVariable String workerId) {
        List<Payroll> payrolls = payrollService.findByWorkerId(workerId);
        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approvePayroll(
            @PathVariable UUID id,
            @Valid @RequestBody PayrollApprovalRequest request) {
        try {
            Payroll payroll;
            if ("ACCEPT".equalsIgnoreCase(request.getAction())) {
                payroll = payrollService.acceptPayroll(id);
            } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
                if (request.getReason() == null || 
                        request.getReason().trim().isEmpty()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("status", "error");
                    error.put("message", 
                            "Rejection reason is required");
                    return ResponseEntity.badRequest().body(error);
                }
                payroll = payrollService.rejectPayroll(id, 
                        request.getReason());
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "error");
                error.put("message", 
                        "Invalid action. Must be ACCEPT or REJECT");
                return ResponseEntity.badRequest().body(error);
            }

            return ResponseEntity.ok(
                    new ApiSuccessResponse<>(payroll));
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/harvest/create")
    public ResponseEntity<?> createPayrollFromHarvest(
            @Valid @RequestBody HarvestPayrollRequest request) {
        try {
            payrollService.createPayrollFromHarvestApproval(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiSuccessResponse<>(
                            "Payroll created successfully from harvest approval"));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/delivery/create")
    public ResponseEntity<?> createPayrollFromDelivery(
            @Valid @RequestBody DeliveryPayrollRequest request) {
        try {
            payrollService.createPayrollFromDeliveryApproval(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiSuccessResponse<>(
                            "Payroll created successfully from delivery approval"));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/approve")
    public void approve(@PathVariable UUID id) {
        payrollService.approvePayroll(id);
    }
    @PutMapping("/{id}/reject")
    public void reject(@PathVariable UUID id, @RequestBody Map<String, String> payload) {
        String reason = payload.get("reason");
        payrollService.rejectPayroll(id, reason);
    }
}