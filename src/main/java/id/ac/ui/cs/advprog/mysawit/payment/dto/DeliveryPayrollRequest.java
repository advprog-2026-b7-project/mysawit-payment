package id.ac.ui.cs.advprog.mysawit.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPayrollRequest {
    private String driverId;
    private String driverName;
    private Double driverAmount;
    private String mandorId;
    private String mandorName;
    private Double mandorAmount;
    private String deliveryId;
    private String driverDescription;
    private String mandorDescription;
}
