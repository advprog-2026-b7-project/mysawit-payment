package id.ac.ui.cs.advprog.mysawit.payment.subscriber;

import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayrollSubscriber {

    private final PayrollService payrollService;

    @EventListener
    @Transactional
    public void handleHarvestApprovedEvent(HarvestApprovedEvent event) {
        Double amount = event.getCalculatedAmount();
        String description = String.format(
            "Harvest Approved - ID: %s | Buruh: %s | "
                    + "Weight: %.2f kg | Price: Rp%.0f/kg | "
                    + "Amount (90%%): Rp%.0f",
            event.getHarvestId(),
            event.getBuruhName(),
            event.getWeightKg(),
            event.getPricePerKg(),
            amount
        );

        HarvestPayrollRequest request = new HarvestPayrollRequest(
            event.getBuruhId(),
            event.getBuruhName(),
            amount,
            event.getHarvestId(),
            description
        );

        payrollService.createPayrollFromHarvestApproval(request);
    }
}

