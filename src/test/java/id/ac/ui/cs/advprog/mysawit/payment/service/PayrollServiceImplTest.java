package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository
        .PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.payment.service.gateway
        .PaymentGateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
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
        when(payrollRepository.findById(payrollId))
                .thenReturn(pendingPayroll);

        when(
                paymentGateway.processPayment(
                        anyDouble(),
                        anyString()
                )
        ).thenReturn(true);

        payrollService.approvePayroll(payrollId);

        assertEquals(
                "SUCCESS",
                pendingPayroll.getStatus()
        );

        verify(payrollRepository, times(1))
                .save(pendingPayroll);
    }

    @Test
    void testRejectPayrollLogic() {
        String reason = "Budget tidak mencukupi";

        when(payrollRepository.findById(payrollId))
                .thenReturn(pendingPayroll);

        payrollService.rejectPayroll(
                payrollId,
                reason
        );

        assertEquals(
                "REJECTED",
                pendingPayroll.getStatus()
        );

        assertEquals(
                reason,
                pendingPayroll.getRejectionReason()
        );

        verify(payrollRepository, times(1))
                .save(pendingPayroll);
    }

    @Test
    void testApprovePayroll_WhenPayrollNotFound_ShouldDoNothing() {
        UUID randomId = UUID.randomUUID();

        when(payrollRepository.findById(randomId))
                .thenReturn(null);

        payrollService.approvePayroll(randomId);

        verify(payrollRepository, never())
                .save(any());
    }

    @Test
    void testApprovePayroll_WhenPaymentFails_StatusShouldBeFAILED() {
        when(payrollRepository.findById(payrollId))
                .thenReturn(pendingPayroll);

        when(
                paymentGateway.processPayment(
                        anyDouble(),
                        anyString()
                )
        ).thenReturn(false);

        payrollService.approvePayroll(payrollId);

        assertEquals(
                "FAILED",
                pendingPayroll.getStatus()
        );

        verify(payrollRepository, times(1))
                .save(pendingPayroll);
    }

    @Test
    void testRejectPayroll_WhenPayrollNotFound_ShouldDoNothing() {
        UUID randomId = UUID.randomUUID();

        when(payrollRepository.findById(randomId))
                .thenReturn(null);

        payrollService.rejectPayroll(
                randomId,
                "Some reason"
        );

        verify(payrollRepository, never())
                .save(any());
    }

    @Test
    void testCreatePayrollFromEvent_WhenPaymentSuccess() {
        String workerId = "W-01";
        Double amount = 200000.0;
        String referenceId = "REF-001";

        when(
                paymentGateway.processPayment(
                        amount,
                        "ACC-" + workerId
                )
        ).thenReturn(true);

        payrollService.createPayrollFromEvent(
                workerId,
                amount,
                referenceId
        );

        ArgumentCaptor<Payroll> payrollCaptor =
                ArgumentCaptor.forClass(Payroll.class);

        verify(payrollRepository, times(2))
                .save(payrollCaptor.capture());

        Payroll savedPayroll =
                payrollCaptor.getAllValues().get(1);

        assertEquals(
                "SUCCESS",
                savedPayroll.getStatus()
        );

        assertEquals(
                workerId,
                savedPayroll.getWorkerId()
        );

        assertEquals(
                amount,
                savedPayroll.getAmount()
        );

        assertEquals(
                referenceId,
                savedPayroll.getReferenceId()
        );
    }

    @Test
    void testCreatePayrollFromEvent_WhenPaymentFails() {
        String workerId = "W-02";
        Double amount = 300000.0;
        String referenceId = "REF-002";

        when(
                paymentGateway.processPayment(
                        amount,
                        "ACC-" + workerId
                )
        ).thenReturn(false);

        payrollService.createPayrollFromEvent(
                workerId,
                amount,
                referenceId
        );

        ArgumentCaptor<Payroll> payrollCaptor =
                ArgumentCaptor.forClass(Payroll.class);

        verify(payrollRepository, times(2))
                .save(payrollCaptor.capture());

        Payroll savedPayroll =
                payrollCaptor.getAllValues().get(1);

        assertEquals(
                "FAILED",
                savedPayroll.getStatus()
        );
    }
}