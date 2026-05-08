package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.payment.service.gateway.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceImplTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PayrollServiceImpl payrollService;

    private Payroll pendingPayroll;
    private UUID payrollId;

    @BeforeEach
    void setUp() {
        payrollId = UUID.randomUUID();
        pendingPayroll = new Payroll();
        pendingPayroll.setId(payrollId);
        pendingPayroll.setAmount(100000.0);
        pendingPayroll.setWorkerId("W-01");
        pendingPayroll.setStatus("PENDING");
    }

    @Test
    void testApprovePayrollSuccess() {
        when(payrollRepository.findById(payrollId)).thenReturn(pendingPayroll);
        when(paymentGateway.processPayment(anyDouble(), anyString())).thenReturn(true);

        payrollService.approvePayroll(payrollId);

        assertEquals("SUCCESS", pendingPayroll.getStatus());
        verify(payrollRepository, times(1)).save(pendingPayroll);
    }

    @Test
    void testRejectPayrollLogic() {
        String reason = "Budget tidak mencukupi";
        // Mocking repository findById
        when(payrollRepository.findById(payrollId)).thenReturn(pendingPayroll);

        payrollService.rejectPayroll(payrollId, reason);

        // Assertions
        assertEquals("REJECTED", pendingPayroll.getStatus());
        assertEquals(reason, pendingPayroll.getRejectionReason());
        verify(payrollRepository, times(1)).save(pendingPayroll);
    }
}