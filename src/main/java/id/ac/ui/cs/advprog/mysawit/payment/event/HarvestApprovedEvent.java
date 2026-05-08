package id.ac.ui.cs.advprog.mysawit.payment.event;

import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;

public class HarvestApprovedEvent extends ApplicationEvent {
    private final String harvestId;
    private final String buruhId;
    private final String buruhName;
    private final BigDecimal weightKg;
    private final BigDecimal pricePerKg;

    public HarvestApprovedEvent(Object source, String harvestId, String buruhId,
            String buruhName, BigDecimal weightKg, BigDecimal pricePerKg) {
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

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public BigDecimal getPricePerKg() {
        return pricePerKg;
    }

    public BigDecimal getCalculatedAmount() {
        if (weightKg == null || pricePerKg == null) {
            return BigDecimal.ZERO;
        }
        // Amount = weightKg × pricePerKg × 0.90
        return weightKg.multiply(pricePerKg)
                .multiply(new BigDecimal("0.90"));
    }
}
