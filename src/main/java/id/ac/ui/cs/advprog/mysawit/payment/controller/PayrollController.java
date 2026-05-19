package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public List<Payroll> getPayrollList() {

        return payrollService.findAll();
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