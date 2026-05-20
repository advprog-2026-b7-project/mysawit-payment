package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.payment.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.payment.service.gateway.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollJobQueue {
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final Logger logger = LoggerFactory.getLogger(PayrollJobQueue.class);

    private final PayrollRepository payrollRepository;
    private final PaymentGateway paymentGateway;

    @Async("payrollTaskExecutor")
    @Transactional
    public void processPayrollAsync(UUID payrollId) {
        logger.info("[ASYNC] Starting payroll job for id: {}", payrollId);

        Payroll payroll = payrollRepository.findById(payrollId);

        if (payroll == null) {
            logger.error("[ASYNC] Payroll not found: {}", payrollId);
            return;
        }

        if (!STATUS_PENDING.equals(payroll.getStatus())) {
            logger.warn("[ASYNC] Payroll {} is not PENDING, skipping. Status: {}",
                    payrollId, payroll.getStatus());
            return;
        }

        try {
            payroll.setStatus(STATUS_PROCESSING);
            payrollRepository.save(payroll);

            boolean success = paymentGateway.processPayment(
                    payroll.getAmount(), "ACC-" + payroll.getWorkerId()
            );

            payroll.setStatus(success ? STATUS_SUCCESS : STATUS_FAILED);
            payrollRepository.save(payroll);

            logger.info("[ASYNC] Payroll {} completed with status: {}",
                    payrollId, payroll.getStatus());

        } catch (Exception e) {
            logger.error("[ASYNC] Error processing payroll {}: {}", payrollId, e.getMessage());
            payroll.setStatus(STATUS_FAILED);
            payrollRepository.save(payroll);
        }
    }
}