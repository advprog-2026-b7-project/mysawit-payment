package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceImplTest {

    @Mock
    private PayrollRepository payrollRepository;

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
    void testAcceptPayrollNotPending() {
        UUID payrollId = UUID.randomUUID();
        Payroll payroll = new Payroll();
        payroll.setId(payrollId);
        payroll.setStatus("ACCEPTED");

        when(payrollRepository.findById(payrollId))
                .thenReturn(Optional.of(payroll));

        assertThrows(RuntimeException.class, () -> {
            payrollService.acceptPayroll(payrollId);
        });
    }

    @Test
    void testCreatePayrollFromDeliveryApproval() {
        DeliveryPayrollRequest request = new DeliveryPayrollRequest(
            "DRIVER-001",
            "Ahmad",
            new BigDecimal("3000000.0"),
            "MANDOR-001",
            "Pak Bambang",
            new BigDecimal("2700000.0"),
            "DELIVERY-001",
            "Delivery Approved - Driver",
            "Delivery Approved - Mandor"
        );

        payrollService.createPayrollFromDeliveryApproval(request);

        verify(payrollRepository, times(2)).save(any(Payroll.class));
    }

    @Test
    void testCreatePayrollFromDeliveryApprovalNoMandor() {
        DeliveryPayrollRequest request = new DeliveryPayrollRequest(
            "DRIVER-001",
            "Ahmad",
            new BigDecimal("3000000.0"),
            null,
            null,
            BigDecimal.ZERO,
            "DELIVERY-001",
            "Delivery Approved - Driver",
            "Delivery Approved - Mandor"
        );

        payrollService.createPayrollFromDeliveryApproval(request);

        verify(payrollRepository, times(1)).save(any(Payroll.class));
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