package id.ac.ui.cs.advprog.mysawit.payment.subscriber;

import id.ac.ui.cs.advprog.mysawit.payment.dto.DeliveryPayrollRequest;
import id.ac.ui.cs.advprog.mysawit.payment.event.DeliveryApprovedEvent;
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

        payrollService.createPayrollFromHarvestApproval(
            event.getBuruhId(),
            event.getBuruhName(),
            amount,
            event.getHarvestId(),
            description
        );
    }

    @EventListener
    @Transactional
    public void handleDeliveryApprovedEvent(DeliveryApprovedEvent event) {
        Double driverAmount = event.getDriverAmount();
        Double mandorAmount = event.getMandorAmount();

        String driverDescription = String.format(
            "Delivery Approved - ID: %s | Driver: %s | "
                    + "Weight: %.2f kg | Price: Rp%.0f/kg | "
                    + "Amount: Rp%.0f",
            event.getDeliveryId(),
            event.getDriverName(),
            event.getWeightKg(),
            event.getDriverPricePerKg(),
            driverAmount
        );

        String mandorDescription = String.format(
            "Delivery Approved - ID: %s | Mandor: %s | "
                    + "Weight: %.2f kg | Price: Rp%.0f/kg | "
                    + "Amount (90%%): Rp%.0f",
            event.getDeliveryId(),
            event.getMandorName(),
            event.getWeightKg(),
            event.getMandorPricePerKg(),
            mandorAmount
        );

        DeliveryPayrollRequest request = new DeliveryPayrollRequest(
            event.getDriverId(),
            event.getDriverName(),
            driverAmount,
            event.getMandorId(),
            event.getMandorName(),
            mandorAmount,
            event.getDeliveryId(),
            driverDescription,
            mandorDescription
        );

        payrollService.createPayrollFromDeliveryApproval(request);
    }
}

