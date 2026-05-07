package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceImplTest {

    @Mock
    private PayrollRepository payrollRepository;

    @InjectMocks
    private PayrollServiceImpl payrollService;

    @Test
    void testCreatePayrollFromHarvestApproval() {
        payrollService.createPayrollFromHarvestApproval(
            "BURUH-001",
            "Budi",
            4500000.0,
            "HARVEST-001",
            "Harvest Approved - ID: HARVEST-001"
        );

        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }
}