package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.payment.service.gateway.PaymentGateway;
import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollJobQueue;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_PROCESSING = "PROCESSING";
    private final PayrollRepository payrollRepository;
    private final PaymentGateway paymentGateway;
    private final PayrollJobQueue payrollJobQueue;

    @Override
    public List<Payroll> findAll() {
        return payrollRepository.findAll();
    }

    @Override
    @Transactional
    public void createPayrollFromHarvestApproval(
            HarvestPayrollRequest request) {
        Payroll payroll = new Payroll();
        payroll.setWorkerId(request.getBuruhId());
        payroll.setWorkerName(request.getBuruhName());
        payroll.setAmount(request.getAmount());
        payroll.setReferenceId(request.getHarvestId());
        payroll.setPayrollType("HARVEST");
        payroll.setDescription(request.getDescription());
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

    @Override
    public List<Payroll> findPayrolls(LocalDate tanggal, String status, String workerId) {
        int filterCount = countActiveFilters(tanggal, status, workerId);
        
        return switch (filterCount) {
            case 0 -> payrollRepository.findAll();
            case 1 -> findWithSingleFilter(tanggal, status, workerId);
            case 2 -> findWithTwoFilters(tanggal, status, workerId);
            case 3 -> payrollRepository.findByTanggalAndStatusAndWorkerId(
                    tanggal, status, workerId);
            default -> payrollRepository.findAll();
        };
    }

    private int countActiveFilters(LocalDate tanggal, String status, String workerId) {
        int count = 0;
        if (tanggal != null) {
            count++;
        }
        if (status != null && !status.isEmpty()) {
            count++;
        }
        if (workerId != null && !workerId.isEmpty()) {
            count++;
        }
        return count;
    }

    private List<Payroll> findWithSingleFilter(LocalDate tanggal, String status, String workerId) {
        if (tanggal != null) {
            return payrollRepository.findByTanggal(tanggal);
        } else if (status != null && !status.isEmpty()) {
            return payrollRepository.findByStatus(status);
        } else {
            return payrollRepository.findByWorkerId(workerId);
        }
    }

 
    private List<Payroll> findWithTwoFilters(LocalDate tanggal, String status, String workerId) {
        if (tanggal != null && status != null && !status.isEmpty()) {
            return payrollRepository.findByTanggalAndStatus(tanggal, status);
        } else if (tanggal != null && workerId != null && !workerId.isEmpty()) {
            return payrollRepository.findByTanggalAndWorkerId(tanggal, workerId);
        } else {
            return payrollRepository.findByStatusAndWorkerId(status, workerId);
        }
    }

    @Override
    public List<Payroll> findByPayrollType(String payrollType) {
        return payrollRepository.findByPayrollType(payrollType);
    }

    @Override
    public List<Payroll> findByWorkerId(String workerId) {
        return payrollRepository.findByWorkerId(workerId);
    }
    @Override
    public Payroll findById(UUID id) {
        return payrollRepository.findById(id).orElse(null);
    }
}