package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}