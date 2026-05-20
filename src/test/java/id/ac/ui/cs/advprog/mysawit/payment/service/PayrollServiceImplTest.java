package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollServiceImplTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private PayrollJobQueue payrollJobQueue;

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
        pendingPayroll.setAmount(new BigDecimal("100000.0"));
        pendingPayroll.setWorkerId("W-01");
        pendingPayroll.setStatus("PENDING");
    }

    @Test
    void testCreatePayrollFromHarvestApproval() {
        HarvestPayrollRequest request = new HarvestPayrollRequest(
            "BURUH-001",
            "Budi",
            new BigDecimal("4500000.0"),
            "HARVEST-001",
            "Harvest Approved - ID: HARVEST-001"
        );

        payrollService.createPayrollFromHarvestApproval(request);

        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testAcceptPayroll() {
        UUID payrollId = UUID.randomUUID();
        Payroll payroll = new Payroll();
        payroll.setId(payrollId);
        payroll.setWorkerId("BURUH-001");
        payroll.setAmount(new BigDecimal("4500000.0"));
        payroll.setStatus("PENDING");

        when(payrollRepository.findById(payrollId))
                .thenReturn(Optional.of(payroll));
        when(payrollRepository.save(any(Payroll.class)))
                .thenReturn(payroll);

        Payroll result = payrollService.acceptPayroll(payrollId);

        assertEquals("ACCEPTED", result.getStatus());
        assertNotNull(result.getApprovedAt());
        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testRejectPayroll() {
        UUID payrollId = UUID.randomUUID();
        Payroll payroll = new Payroll();
        payroll.setId(payrollId);
        payroll.setWorkerId("BURUH-001");
        payroll.setAmount(new BigDecimal("4500000.0"));
        payroll.setStatus("PENDING");

        when(payrollRepository.findById(payrollId))
                .thenReturn(Optional.of(payroll));
        when(payrollRepository.save(any(Payroll.class)))
                .thenReturn(payroll);

        Payroll result = payrollService.rejectPayroll(payrollId, 
                "Invalid calculation");

        assertEquals("REJECTED", result.getStatus());
        assertEquals("Invalid calculation", result.getRejectionReason());
        assertNotNull(result.getApprovedAt());
        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testAcceptPayrollNotFound() {
        UUID payrollId = UUID.randomUUID();
        when(payrollRepository.findById(payrollId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            payrollService.acceptPayroll(payrollId);
        });
    }

    @Test
    void testRejectPayrollNotFound() {
        UUID payrollId = UUID.randomUUID();
        when(payrollRepository.findById(payrollId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            payrollService.rejectPayroll(payrollId, "Invalid");
        });
    }

    @Test
    void testCreatePayrollFromEvent_WhenPaymentSuccess() {
        String workerId = "W-01";
        Double amount = 200000.0;
        String referenceId = "REF-001";

        when(payrollRepository.existsByReferenceId(referenceId))
                .thenReturn(false);

        payrollService.createPayrollFromEvent(workerId, amount, referenceId);

        ArgumentCaptor<Payroll> payrollCaptor =
                ArgumentCaptor.forClass(Payroll.class);

        verify(payrollRepository, times(1))
                .save(payrollCaptor.capture());

        Payroll savedPayroll = payrollCaptor.getValue();
        assertEquals("PENDING", savedPayroll.getStatus());
        assertEquals(workerId, savedPayroll.getWorkerId());
        assertEquals(amount, savedPayroll.getAmount());
        assertEquals(referenceId, savedPayroll.getReferenceId());

        verify(payrollJobQueue, times(1))
                .processPayrollAsync(any());

        verify(paymentGateway, never())
                .processPayment(anyDouble(), anyString());
    }

    @Test
    void testCreatePayrollFromEvent_WhenPaymentFails() {
        String workerId = "W-02";
        Double amount = 300000.0;
        String referenceId = "REF-002";

        when(payrollRepository.existsByReferenceId(referenceId))
                .thenReturn(false);

        payrollService.createPayrollFromEvent(workerId, amount, referenceId);

        verify(payrollRepository, times(1)).save(any());
        verify(payrollJobQueue, times(1)).processPayrollAsync(any());
        verify(paymentGateway, never()).processPayment(anyDouble(), anyString());
    }
    @Test
    void testCreatePayrollFromEvent_WhenDuplicateReferenceId_ShouldSkip() {
        when(payrollRepository.existsByReferenceId("REF-001"))
                .thenReturn(true);

        payrollService.createPayrollFromEvent("W-01", 200000.0, "REF-001");

        verify(payrollRepository, never()).save(any());
        verify(payrollJobQueue, never()).processPayrollAsync(any());
    }
}
@ExtendWith(MockitoExtension.class)
class PayrollJobQueueTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PayrollJobQueue payrollJobQueue;

    @Test
    void testProcessPayrollAsync_Success() {
        UUID id = UUID.randomUUID();
        Payroll payroll = new Payroll();
        payroll.setId(id);
        payroll.setStatus("PENDING");
        payroll.setAmount(100000.0);
        payroll.setWorkerId("W-01");

        when(payrollRepository.findById(id)).thenReturn(payroll);
        when(paymentGateway.processPayment(anyDouble(), anyString())).thenReturn(true);

        payrollJobQueue.processPayrollAsync(id);

        assertEquals("SUCCESS", payroll.getStatus());
        verify(payrollRepository, times(2)).save(payroll); // PROCESSING lalu SUCCESS
    }

    @Test
    void testProcessPayrollAsync_Failed() {
        UUID id = UUID.randomUUID();
        Payroll payroll = new Payroll();
        payroll.setId(id);
        payroll.setStatus("PENDING");
        payroll.setAmount(100000.0);
        payroll.setWorkerId("W-01");

        when(payrollRepository.findById(id)).thenReturn(payroll);
        when(paymentGateway.processPayment(anyDouble(), anyString())).thenReturn(false);

        payrollJobQueue.processPayrollAsync(id);

        assertEquals("FAILED", payroll.getStatus());
    }

    @Test
    void testProcessPayrollAsync_PayrollNotFound_ShouldDoNothing() {
        UUID id = UUID.randomUUID();
        when(payrollRepository.findById(id)).thenReturn(null);

        payrollJobQueue.processPayrollAsync(id);

        verify(payrollRepository, never()).save(any());
    }

    @Test
    void testProcessPayrollAsync_NotPending_ShouldSkip() {
        UUID id = UUID.randomUUID();
        Payroll payroll = new Payroll();
        payroll.setId(id);
        payroll.setStatus("SUCCESS"); // bukan PENDING

        when(payrollRepository.findById(id)).thenReturn(payroll);

        payrollJobQueue.processPayrollAsync(id);

        verify(payrollRepository, never()).save(any());
        verify(paymentGateway, never()).processPayment(anyDouble(), anyString());
    }
}