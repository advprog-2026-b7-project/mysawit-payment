package id.ac.ui.cs.advprog.mysawit.payment.event;

import id.ac.ui.cs.advprog.mysawit.payment.dto.HarvestPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayrollEventListener {

    private final PayrollService payrollService;

    @EventListener
    @Async("payrollTaskExecutor")
    public void handleHarvestApproved(HarvestApprovedEvent event) {
        HarvestPayrollRequest request = new HarvestPayrollRequest(
                event.getBuruhId(),
                event.getBuruhName(),
                event.getCalculatedAmount(),
                event.getHarvestId(),
                "Upah panen: " + event.getWeightKg() + " kg × Rp"
                        + event.getPricePerKg() + "/kg × 90%"
        );
        payrollService.createPayrollFromHarvestApproval(request);
    }

    @EventListener
    @Async("payrollTaskExecutor")
    public void handleDeliveryApproved(DeliveryApprovedEvent event) {
        DeliveryPayrollRequest request = new DeliveryPayrollRequest(
                event.getDriverId(),
                event.getDriverName(),
                event.getDriverAmount(),
                event.getMandorId(),
                event.getMandorName(),
                event.getMandorAmount(),
                event.getDeliveryId(),
                "Upah supir: " + event.getWeightKg() + " kg × Rp"
                        + event.getDriverPricePerKg() + "/kg × 90%",
                "Upah mandor: " + event.getWeightKg() + " kg × Rp"
                        + event.getMandorPricePerKg() + "/kg × 90%"
        );
        payrollService.createPayrollFromDeliveryApproval(request);
    }
}