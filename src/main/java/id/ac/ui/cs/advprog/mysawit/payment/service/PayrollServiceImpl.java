package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
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
}