package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void testAcceptPayroll() {
        UUID payrollId = UUID.randomUUID();
        Payroll payroll = new Payroll();
        payroll.setId(payrollId);
        payroll.setWorkerId("BURUH-001");
        payroll.setAmount(4500000.0);
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
        payroll.setAmount(4500000.0);
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
            3000000.0,
            "MANDOR-001",
            "Pak Bambang",
            2700000.0,
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
            3000000.0,
            null,
            null,
            0.0,
            "DELIVERY-001",
            "Delivery Approved - Driver",
            "Delivery Approved - Mandor"
        );

        payrollService.createPayrollFromDeliveryApproval(request);

        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }
}