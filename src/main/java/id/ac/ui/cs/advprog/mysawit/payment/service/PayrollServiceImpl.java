package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;

    @Override
    public List<Payroll> findAll() {
        return payrollRepository.findAll();
    }

    @Override
    @Transactional
    public void createPayrollFromHarvestApproval(String buruhId,
            String buruhName, Double amount, String harvestId,
            String description) {
        Payroll payroll = new Payroll();
        payroll.setWorkerId(buruhId);
        payroll.setWorkerName(buruhName);
        payroll.setAmount(amount);
        payroll.setReferenceId(harvestId);
        payroll.setPayrollType("HARVEST");
        payroll.setDescription(description);
        payroll.setStatus("PENDING");

        payrollRepository.save(payroll);
    }

    @Override
    @Transactional
    public void createPayrollFromDeliveryApproval(
            DeliveryPayrollRequest request) {
        Payroll driverPayroll = new Payroll();
        driverPayroll.setWorkerId(request.getDriverId());
        driverPayroll.setWorkerName(request.getDriverName());
        driverPayroll.setAmount(request.getDriverAmount());
        driverPayroll.setReferenceId(request.getDeliveryId());
        driverPayroll.setPayrollType("DELIVERY");
        driverPayroll.setDescription(request.getDriverDescription());
        driverPayroll.setStatus("PENDING");

        payrollRepository.save(driverPayroll);

        if (request.getMandorId() != null 
                && !request.getMandorId().trim().isEmpty()) {
            Payroll mandorPayroll = new Payroll();
            mandorPayroll.setWorkerId(request.getMandorId());
            mandorPayroll.setWorkerName(request.getMandorName());
            mandorPayroll.setAmount(request.getMandorAmount());
            mandorPayroll.setReferenceId(request.getDeliveryId());
            mandorPayroll.setPayrollType("DELIVERY");
            mandorPayroll.setDescription(
                    request.getMandorDescription());
            mandorPayroll.setStatus("PENDING");

            payrollRepository.save(mandorPayroll);
        }
    }

    @Override
    @Transactional
    public Payroll acceptPayroll(UUID payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException(
                        "Payroll not found: " + payrollId));

        if (!payroll.getStatus().equals("PENDING")) {
            throw new RuntimeException(
                    "Payroll status must be PENDING to accept");
        }

        payroll.setStatus("ACCEPTED");
        payroll.setApprovedAt(LocalDateTime.now());
        return payrollRepository.save(payroll);
    }

    @Override
    @Transactional
    public Payroll rejectPayroll(UUID payrollId, String reason) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException(
                        "Payroll not found: " + payrollId));

        if (!payroll.getStatus().equals("PENDING")) {
            throw new RuntimeException(
                    "Payroll status must be PENDING to reject");
        }

        payroll.setStatus("REJECTED");
        payroll.setRejectionReason(reason);
        payroll.setApprovedAt(LocalDateTime.now());
        return payrollRepository.save(payroll);
    }
}