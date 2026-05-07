package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.dto.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payroll")
@CrossOrigin(origins = "http://localhost:3000")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @GetMapping("/list")
    public ResponseEntity<ApiSuccessResponse<List<Payroll>>> getPayrollList(
            @RequestParam(required = false) String tanggal,
            @RequestParam(required = false) String status) {
        List<Payroll> payrolls = payrollService.findAll();

        if (tanggal != null && !tanggal.isEmpty()) {
            LocalDate filterDate = LocalDate.parse(tanggal);
            payrolls = payrolls.stream()
                    .filter(p -> p.getTanggal().equals(filterDate))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty()) {
            payrolls = payrolls.stream()
                    .filter(p -> p.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }

    @GetMapping("/by-type/{payrollType}")
    public ResponseEntity<ApiSuccessResponse<List<Payroll>>>
            getPayrollByType(@PathVariable String payrollType) {
        List<Payroll> payrolls = payrollService.findAll().stream()
                .filter(p -> p.getPayrollType().equalsIgnoreCase(payrollType))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiSuccessResponse<>(payrolls));
    }
}