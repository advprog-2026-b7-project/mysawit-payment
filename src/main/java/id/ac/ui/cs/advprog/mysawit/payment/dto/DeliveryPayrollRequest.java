package id.ac.ui.cs.advprog.mysawit.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPayrollRequest {
    @NotBlank(message = "Driver ID cannot be blank")
    private String driverId;
    
    @NotBlank(message = "Driver name cannot be blank")
    private String driverName;
    
    @NotNull(message = "Driver amount cannot be null")
    @Positive(message = "Driver amount must be positive")
    private BigDecimal driverAmount;
    
    private String mandorId;
    private String mandorName;
    
    @NotNull(message = "Mandor amount cannot be null")
    @Positive(message = "Mandor amount must be positive")
    private BigDecimal mandorAmount;
    
    @NotBlank(message = "Delivery ID cannot be blank")
    private String deliveryId;
    
    @NotBlank(message = "Driver description cannot be blank")
    private String driverDescription;
    
    private String mandorDescription;
}
