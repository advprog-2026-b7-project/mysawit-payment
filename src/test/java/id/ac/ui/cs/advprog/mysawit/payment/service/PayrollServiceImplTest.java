package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.payment.service.gateway.PaymentGateway;
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

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PayrollServiceImpl payrollService;

    @Test
    void testCreatePayrollFromEvent_Success() {
        when(paymentGateway.processPayment(anyDouble(), anyString())).thenReturn(true);

        payrollService.createPayrollFromEvent("Vidia", 500000.0, "REF-123");

        verify(payrollRepository, times(2)).save(any(Payroll.class));
        verify(paymentGateway).processPayment(500000.0, "ACC-Vidia");
    }
}