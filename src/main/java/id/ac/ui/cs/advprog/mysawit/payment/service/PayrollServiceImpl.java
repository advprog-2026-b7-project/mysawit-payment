package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.payment.service.gateway.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void createPayrollFromEvent(String workerId, Double amount, String referenceId) {
        if (payrollRepository.existsByReferenceId(referenceId)) {
            return;
        }

        Payroll payroll = new Payroll();
        payroll.setWorkerId(workerId);
        payroll.setAmount(amount);
        payroll.setReferenceId(referenceId);
        payroll.setStatus(STATUS_PENDING);
        payrollRepository.save(payroll);

        payrollJobQueue.processPayrollAsync(payroll.getId());
    }
    @Override
    @Transactional
    public void approvePayroll(UUID id) {
        Payroll payroll = payrollRepository.findById(id);
        if (payroll != null && STATUS_PENDING.equals(payroll.getStatus())) {
            boolean success = paymentGateway.processPayment(
                    payroll.getAmount(),"ACC-" + payroll.getWorkerId());
            payroll.setStatus(success ? STATUS_SUCCESS : STATUS_FAILED);
            payrollRepository.save(payroll);
        }
    }

    @Override
    @Transactional
    public void rejectPayroll(UUID id, String reason) {
        Payroll payroll = payrollRepository.findById(id);
        if (payroll != null && STATUS_PENDING.equals(payroll.getStatus())) {
            payroll.setStatus(STATUS_REJECTED);
            payroll.setRejectionReason(reason);
            payrollRepository.save(payroll);
        }
    }
    @Override
    public Payroll findById(UUID id) {
        return payrollRepository.findById(id);
    }
}