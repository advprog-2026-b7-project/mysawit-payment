package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.payment.service.gateway.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final PaymentGateway paymentGateway;

    @Override
    public List<Payroll> findAll() {
        return payrollRepository.findAll();
    }

    @Override
    @Transactional
    public void createPayrollFromEvent(String workerId, Double amount, String referenceId) {

        Payroll payroll = new Payroll();
        payroll.setWorkerId(workerId);
        payroll.setAmount(amount);
        payroll.setReferenceId(referenceId);
        payroll.setStatus("PENDING");
        payrollRepository.save(payroll);

        boolean success = paymentGateway.processPayment(amount, "ACC-" + workerId);

        payroll.setStatus(success ? "SUCCESS" : "FAILED");
        payrollRepository.save(payroll);
    }
}