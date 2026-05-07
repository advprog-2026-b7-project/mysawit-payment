package id.ac.ui.cs.advprog.mysawit.payment.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter @Setter
public class DeliveryApprovedEvent extends ApplicationEvent {
    private String deliveryId;
    private String driverId;
    private String driverName;
    private String mandorId;
    private String mandorName;
    private Double weightKg;
    private Double driverPricePerKg;
    private Double mandorPricePerKg;

    public DeliveryApprovedEvent(Object source, String deliveryId,
            String driverId, String driverName, String mandorId,
            String mandorName, Double weightKg, Double driverPricePerKg,
            Double mandorPricePerKg) {
        super(source);
        this.deliveryId = deliveryId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.mandorId = mandorId;
        this.mandorName = mandorName;
        this.weightKg = weightKg;
        this.driverPricePerKg = driverPricePerKg;
        this.mandorPricePerKg = mandorPricePerKg;
    }

    public DeliveryApprovedEvent(Object source, String deliveryId,
            String supirTrukId, String supirTrukName, Double weightKg,
            Double pricePerKg) {
        super(source);
        this.deliveryId = deliveryId;
        this.driverId = supirTrukId;
        this.driverName = supirTrukName;
        this.mandorId = null;
        this.mandorName = null;
        this.weightKg = weightKg;
        this.driverPricePerKg = pricePerKg;
        this.mandorPricePerKg = 0.0;
    }

    public String getSupirTrukId() {
        return driverId;
    }

    public String getSupirTrukName() {
        return driverName;
    }

    public Double getPricePerKg() {
        return driverPricePerKg;
    }

    public Double getDriverAmount() {
        return weightKg * driverPricePerKg;
    }

    public Double getMandorAmount() {
        return mandorId != null ? weightKg * mandorPricePerKg * 0.90 : 0.0;
    }

    public Double getCalculatedAmount() {
        return getDriverAmount();
    }
}
