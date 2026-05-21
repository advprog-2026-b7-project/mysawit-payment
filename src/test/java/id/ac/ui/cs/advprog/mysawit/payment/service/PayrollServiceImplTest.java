package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

    private UUID payrollId;
    private Payroll pendingPayroll;

    @BeforeEach
    void setUp() {
        payrollId = UUID.randomUUID();
        pendingPayroll = new Payroll();
        pendingPayroll.setId(payrollId);
        pendingPayroll.setStatus("PENDING");
        pendingPayroll.setWorkerId("BURUH-001");
        pendingPayroll.setAmount(new BigDecimal("4500000.0"));
    }

    @Test
    void testFindAll() {
        when(payrollRepository.findAll()).thenReturn(List.of(pendingPayroll));

        List<Payroll> result = payrollService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void testCreatePayrollFromHarvestApproval() {
        HarvestPayrollRequest request = new HarvestPayrollRequest(
                "BURUH-001",
                "Budi",
                new BigDecimal("4500000.0"),
                "HARVEST-001",
                "Deskripsi"
        );

        payrollService.createPayrollFromHarvestApproval(request);

        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testCreatePayrollFromDeliveryApproval_DriverOnly() {
        DeliveryPayrollRequest request = mock(DeliveryPayrollRequest.class);
        when(request.getMandorId()).thenReturn(null);

        payrollService.createPayrollFromDeliveryApproval(request);

        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testCreatePayrollFromDeliveryApproval_WithMandor() {
        DeliveryPayrollRequest request = mock(DeliveryPayrollRequest.class);
        when(request.getMandorId()).thenReturn("M-01");

        payrollService.createPayrollFromDeliveryApproval(request);

        verify(payrollRepository, times(2)).save(any(Payroll.class));
    }

    @Test
    void testAcceptPayroll_Success() {
        when(payrollRepository.findById(payrollId)).thenReturn(Optional.of(pendingPayroll));
        when(payrollRepository.save(any(Payroll.class))).thenReturn(pendingPayroll);

        Payroll result = payrollService.acceptPayroll(payrollId);

        assertEquals("ACCEPTED", result.getStatus());
        assertNotNull(result.getApprovedAt());
        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testAcceptPayroll_NotPending() {
        pendingPayroll.setStatus("ACCEPTED");
        when(payrollRepository.findById(payrollId)).thenReturn(Optional.of(pendingPayroll));

        assertThrows(RuntimeException.class, () -> payrollService.acceptPayroll(payrollId));
    }

    @Test
    void testAcceptPayroll_NotFound() {
        when(payrollRepository.findById(payrollId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> payrollService.acceptPayroll(payrollId));
    }

    @Test
    void testRejectPayroll_Success() {
        when(payrollRepository.findById(payrollId)).thenReturn(Optional.of(pendingPayroll));
        when(payrollRepository.save(any(Payroll.class))).thenReturn(pendingPayroll);

        Payroll result = payrollService.rejectPayroll(payrollId, "Invalid");

        assertEquals("REJECTED", result.getStatus());
        assertEquals("Invalid", result.getRejectionReason());
        assertNotNull(result.getApprovedAt());
        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testRejectPayroll_NotPending() {
        pendingPayroll.setStatus("ACCEPTED");
        when(payrollRepository.findById(payrollId)).thenReturn(Optional.of(pendingPayroll));

        assertThrows(RuntimeException.class, () -> payrollService.rejectPayroll(
                payrollId, "Invalid"));
    }

    @Test
    void testRejectPayroll_NotFound() {
        when(payrollRepository.findById(payrollId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> payrollService.rejectPayroll(
                payrollId, "Invalid"));
    }

    @Test
    void testFindPayrolls_ZeroFilters() {
        Pageable pageable = mock(Pageable.class);

        payrollService.findPayrolls(null, null, null, pageable);

        verify(payrollRepository).findAll(pageable);
    }

    @Test
    void testFindPayrolls_OneFilter_Tanggal() {
        Pageable pageable = mock(Pageable.class);
        LocalDate date = LocalDate.now();

        payrollService.findPayrolls(date, null, null, pageable);

        verify(payrollRepository).findByTanggal(date, pageable);
    }

    @Test
    void testFindPayrolls_OneFilter_Status() {
        Pageable pageable = mock(Pageable.class);

        payrollService.findPayrolls(null, "PENDING", null, pageable);

        verify(payrollRepository).findByStatus("PENDING", pageable);
    }

    @Test
    void testFindPayrolls_OneFilter_WorkerId() {
        Pageable pageable = mock(Pageable.class);

        payrollService.findPayrolls(null, null, "W-01", pageable);

        verify(payrollRepository).findByWorkerId("W-01", pageable);
    }

    @Test
    void testFindPayrolls_TwoFilters_TanggalStatus() {
        Pageable pageable = mock(Pageable.class);
        LocalDate date = LocalDate.now();

        payrollService.findPayrolls(date, "PENDING", null, pageable);

        verify(payrollRepository).findByTanggalAndStatus(date, "PENDING", pageable);
    }

    @Test
    void testFindPayrolls_TwoFilters_TanggalWorker() {
        Pageable pageable = mock(Pageable.class);
        LocalDate date = LocalDate.now();

        payrollService.findPayrolls(date, null, "W-01", pageable);

        verify(payrollRepository).findByTanggalAndWorkerId(date, "W-01", pageable);
    }

    @Test
    void testFindPayrolls_TwoFilters_StatusWorker() {
        Pageable pageable = mock(Pageable.class);

        payrollService.findPayrolls(null, "PENDING", "W-01", pageable);

        verify(payrollRepository).findByStatusAndWorkerId("PENDING", "W-01", pageable);
    }

    @Test
    void testFindPayrolls_ThreeFilters() {
        Pageable pageable = mock(Pageable.class);
        LocalDate date = LocalDate.now();

        payrollService.findPayrolls(date, "PENDING", "W-01", pageable);

        verify(payrollRepository).findByTanggalAndStatusAndWorkerId(date,
                "PENDING", "W-01", pageable);
    }

    @Test
    void testFindByPayrollType() {
        Pageable pageable = mock(Pageable.class);

        payrollService.findByPayrollType("HARVEST", pageable);

        verify(payrollRepository).findByPayrollType("HARVEST", pageable);
    }

    @Test
    void testFindByWorkerId() {
        Pageable pageable = mock(Pageable.class);

        payrollService.findByWorkerId("W-01", pageable);

        verify(payrollRepository).findByWorkerId("W-01", pageable);
    }

    @Test
    void testFindById() {
        when(payrollRepository.findById(payrollId)).thenReturn(Optional.of(pendingPayroll));

        Payroll result = payrollService.findById(payrollId);

        assertNotNull(result);
    }

    @Test
    void testFindById_NotFound() {
        when(payrollRepository.findById(payrollId)).thenReturn(Optional.empty());

        Payroll result = payrollService.findById(payrollId);

        assertNull(result);
    }
}