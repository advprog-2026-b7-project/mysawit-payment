package id.ac.ui.cs.advprog.mysawit.payment.event;

import org.springframework.context.ApplicationEvent;

public class HarvestApprovedEvent extends ApplicationEvent {
    private final String harvestId;
    private final String buruhId;
    private final String buruhName;
    private final Double weightKg;
    private final Double pricePerKg;

    public HarvestApprovedEvent(Object source, String harvestId, String buruhId,
            String buruhName, Double weightKg, Double pricePerKg) {
        super(source);
        this.harvestId = harvestId;
        this.buruhId = buruhId;
        this.buruhName = buruhName;
        this.weightKg = weightKg;
        this.pricePerKg = pricePerKg;
    }

    public String getHarvestId() {
        return harvestId;
    }

    public String getBuruhId() {
        return buruhId;
    }

    public String getBuruhName() {
        return buruhName;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public Double getPricePerKg() {
        return pricePerKg;
    }

    public Double getCalculatedAmount() {
        return weightKg * pricePerKg * 0.90;
    }
}
