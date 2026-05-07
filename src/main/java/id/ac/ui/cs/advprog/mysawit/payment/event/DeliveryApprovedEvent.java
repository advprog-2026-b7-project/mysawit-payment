package id.ac.ui.cs.advprog.mysawit.payment.event;

import org.springframework.context.ApplicationEvent;

public class DeliveryApprovedEvent extends ApplicationEvent {
    private final String deliveryId;
    private final String supirTrukId;
    private final String supirTrukName;
    private final Double weightKg;
    private final Double pricePerKg;

    public DeliveryApprovedEvent(Object source, String deliveryId,
            String supirTrukId, String supirTrukName, Double weightKg,
            Double pricePerKg) {
        super(source);
        this.deliveryId = deliveryId;
        this.supirTrukId = supirTrukId;
        this.supirTrukName = supirTrukName;
        this.weightKg = weightKg;
        this.pricePerKg = pricePerKg;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public String getSupirTrukId() {
        return supirTrukId;
    }

    public String getSupirTrukName() {
        return supirTrukName;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public Double getPricePerKg() {
        return pricePerKg;
    }

    public Double getCalculatedAmount() {
        return weightKg * pricePerKg;
    }
}
