package id.ac.ui.cs.advprog.mysawit.payment.repository;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PayrollRepository.class)
class PayrollRepositoryTest {

    @Autowired
    private PayrollRepository payrollRepository;

    @Test
    void testSaveAndFindById() {
        Payroll payroll = new Payroll();
        payroll.setWorkerId("WORKER-TEST");
        payrollRepository.save(payroll);

        assertNotNull(payroll.getId());
        Payroll found = payrollRepository.findById(payroll.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("WORKER-TEST", found.getWorkerId());
    }
}