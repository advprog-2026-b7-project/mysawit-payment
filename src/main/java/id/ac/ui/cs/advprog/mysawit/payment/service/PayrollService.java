package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import java.util.List;
import java.util.UUID;

public interface PayrollService {
    List<Payroll> findAll();

    void createPayrollFromHarvestApproval(HarvestPayrollRequest request);

    void createPayrollFromDeliveryApproval(DeliveryPayrollRequest request);

    Payroll acceptPayroll(UUID payrollId);

    Payroll rejectPayroll(UUID payrollId, String reason);
}