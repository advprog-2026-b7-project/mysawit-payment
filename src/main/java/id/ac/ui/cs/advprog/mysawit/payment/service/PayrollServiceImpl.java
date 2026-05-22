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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private static final String STATUS_ACCEPTED = "ACCEPTED";
    private static final String STATUS_REJECTED = "REJECTED";
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
        String referenceId = buildReference("HARVEST", request.getHarvestId(), request.getBuruhId());
        if (payrollRepository.existsByReferenceId(referenceId)) {
            throw new RuntimeException("Payroll already exists for harvest: " + request.getHarvestId());
        }
        Payroll payroll = new Payroll();
        payroll.setWorkerId(request.getBuruhId());
        payroll.setWorkerName(request.getBuruhName());
        payroll.setAmount(request.getAmount());
        payroll.setReferenceId(referenceId);
        payroll.setPayrollType("HARVEST");
        payroll.setDescription(request.getDescription());
        payroll.setStatus(STATUS_PENDING);

        payrollRepository.save(payroll);
    }

    @Override
    @Transactional
    public void createPayrollFromDeliveryApproval(
            DeliveryPayrollRequest request) {
        boolean createdAny = false;
        if (isNotBlank(request.getDriverId()) && request.getDriverAmount() != null) {
            String driverReferenceId = buildReference(
                    "DELIVERY_DRIVER", request.getDeliveryId(), request.getDriverId());
            if (!payrollRepository.existsByReferenceId(driverReferenceId)) {
                Payroll driverPayroll = new Payroll();
                driverPayroll.setWorkerId(request.getDriverId());
                driverPayroll.setWorkerName(request.getDriverName());
                driverPayroll.setAmount(request.getDriverAmount());
                driverPayroll.setReferenceId(driverReferenceId);
                driverPayroll.setPayrollType("DELIVERY_DRIVER");
                driverPayroll.setDescription(request.getDriverDescription());
                driverPayroll.setStatus(STATUS_PENDING);
                payrollRepository.save(driverPayroll);
                createdAny = true;
            }
        }

        if (isNotBlank(request.getMandorId()) && request.getMandorAmount() != null) {
            String mandorReferenceId = buildReference(
                    "DELIVERY_MANDOR", request.getDeliveryId(), request.getMandorId());
            if (payrollRepository.existsByReferenceId(mandorReferenceId)) {
                if (!createdAny) {
                    throw new RuntimeException(
                            "Payroll already exists for delivery: " + request.getDeliveryId());
                }
                return;
            }
            Payroll mandorPayroll = new Payroll();
            mandorPayroll.setWorkerId(request.getMandorId());
            mandorPayroll.setWorkerName(request.getMandorName());
            mandorPayroll.setAmount(request.getMandorAmount());
            mandorPayroll.setReferenceId(mandorReferenceId);
            mandorPayroll.setPayrollType("DELIVERY_MANDOR");
            mandorPayroll.setDescription(
                    request.getMandorDescription());
            mandorPayroll.setStatus(STATUS_PENDING);

            payrollRepository.save(mandorPayroll);
            createdAny = true;
        }

        if (!createdAny) {
            throw new RuntimeException("No payable delivery recipient was provided");
        }
    }

    @Override
    @Transactional
    public Payroll acceptPayroll(UUID payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException(
                        "Payroll not found: " + payrollId));

        if (!payroll.getStatus().equals(STATUS_PENDING)) {
            throw new RuntimeException(
                    "Payroll status must be PENDING to accept");
        }

        boolean paymentSuccess = paymentGateway.processPayment(
                payroll.getAmount() != null ? payroll.getAmount().doubleValue() : 0.0,
                "ACC-" + payroll.getWorkerId()
        );
        if (!paymentSuccess) {
            throw new RuntimeException("Payment gateway failed to process payroll");
        }

        payroll.setStatus(STATUS_ACCEPTED);
        payroll.setApprovedAt(LocalDateTime.now());
        return payrollRepository.save(payroll);
    }

    @Override
    @Transactional
    public Payroll rejectPayroll(UUID payrollId, String reason) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException(
                        "Payroll not found: " + payrollId));

        if (!payroll.getStatus().equals(STATUS_PENDING)) {
            throw new RuntimeException(
                    "Payroll status must be PENDING to reject");
        }

        payroll.setStatus(STATUS_REJECTED);
        payroll.setRejectionReason(reason);
        payroll.setApprovedAt(LocalDateTime.now());
        return payrollRepository.save(payroll);
    }

    private String buildReference(String prefix, String sourceId, String workerId) {
        return prefix + ":" + sourceId + ":" + workerId;
    }

    @Override
    public Page<Payroll> findPayrolls(LocalDate tanggal, String status, String workerId,
                                       Pageable pageable) {
        int filterCount = countActiveFilters(tanggal, status, workerId);
        
        return switch (filterCount) {
            case 0 -> payrollRepository.findAll(pageable);
            case 1 -> findWithSingleFilter(tanggal, status, workerId, pageable);
            case 2 -> findWithTwoFilters(tanggal, status, workerId, pageable);
            case 3 -> payrollRepository.findByTanggalAndStatusAndWorkerId(
                    tanggal, status, workerId, pageable);
            default -> payrollRepository.findAll(pageable);
        };
    }

    private int countActiveFilters(LocalDate tanggal, String status, String workerId) {
        return (tanggal != null ? 1 : 0) +
               (isNotEmpty(status) ? 1 : 0) +
               (isNotEmpty(workerId) ? 1 : 0);
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private Page<Payroll> findWithSingleFilter(LocalDate tanggal, String status,
                                                String workerId, Pageable pageable) {
        if (tanggal != null) {
            return payrollRepository.findByTanggal(tanggal, pageable);
        }
        if (isNotEmpty(status)) {
            return payrollRepository.findByStatus(status, pageable);
        }
        return payrollRepository.findByWorkerId(workerId, pageable);
    }

    private Page<Payroll> findWithTwoFilters(LocalDate tanggal, String status,
                                              String workerId, Pageable pageable) {
        if (tanggal != null && isNotEmpty(status)) {
            return payrollRepository.findByTanggalAndStatus(tanggal, status, pageable);
        }
        if (tanggal != null && isNotEmpty(workerId)) {
            return payrollRepository.findByTanggalAndWorkerId(tanggal, workerId, pageable);
        }
        return payrollRepository.findByStatusAndWorkerId(status, workerId, pageable);
    }

    @Override
    public Page<Payroll> findByPayrollType(String payrollType, Pageable pageable) {
        return payrollRepository.findByPayrollType(payrollType, pageable);
    }

    @Override
    public Page<Payroll> findByWorkerId(String workerId, Pageable pageable) {
        return payrollRepository.findByWorkerId(workerId, pageable);
    }
    @Override
    public Payroll findById(UUID id) {
        return payrollRepository.findById(id).orElse(null);
    }
}
