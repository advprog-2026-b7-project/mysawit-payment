package id.ac.ui.cs.advprog.mysawit.payment.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;

@Getter @Setter
public class DeliveryApprovedEvent extends ApplicationEvent {
    private String deliveryId;
    private String driverId;
    private String driverName;
    private String mandorId;
    private String mandorName;
    private BigDecimal weightKg;
    private BigDecimal driverPricePerKg;
    private BigDecimal mandorPricePerKg;

    public DeliveryApprovedEvent(Object source, String deliveryId,
            String driverId, String driverName, String mandorId,
            String mandorName, BigDecimal weightKg, BigDecimal driverPricePerKg,
            BigDecimal mandorPricePerKg) {
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
            String supirTrukId, String supirTrukName, BigDecimal weightKg,
            BigDecimal pricePerKg) {
        super(source);
        this.deliveryId = deliveryId;
        this.driverId = supirTrukId;
        this.driverName = supirTrukName;
        this.mandorId = null;
        this.mandorName = null;
        this.weightKg = weightKg;
        this.driverPricePerKg = pricePerKg;
        this.mandorPricePerKg = BigDecimal.ZERO;
    }

    public String getSupirTrukId() {
        return driverId;
    }

    public String getSupirTrukName() {
        return driverName;
    }

    public BigDecimal getPricePerKg() {
        return driverPricePerKg;
    }

    public BigDecimal getDriverAmount() {
        if (weightKg == null || driverPricePerKg == null) {
            return BigDecimal.ZERO;
        }
        // Amount = weightKg × pricePerKg × 0.90
        return weightKg.multiply(driverPricePerKg)
                .multiply(new BigDecimal("0.90"));
    }

    public BigDecimal getMandorAmount() {
        if (mandorId == null || mandorId.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        if (weightKg == null || mandorPricePerKg == null) {
            return BigDecimal.ZERO;
        }
        // Amount = weightKg × pricePerKg × 0.90
        return weightKg.multiply(mandorPricePerKg)
                .multiply(new BigDecimal("0.90"));
    }

    public BigDecimal getCalculatedAmount() {
        return getDriverAmount();
    }
}
