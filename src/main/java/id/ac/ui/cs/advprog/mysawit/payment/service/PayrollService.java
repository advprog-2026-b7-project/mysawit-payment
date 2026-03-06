package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import java.util.List;

public interface PayrollService {
    List<Payroll> findAll();
    void createPayrollFromEvent(String workerId, Double amount, String referenceId);
}