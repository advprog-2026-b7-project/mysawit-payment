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
public class HarvestPayrollRequest {
    @NotBlank(message = "Buruh ID cannot be blank")
    private String buruhId;
    
    @NotBlank(message = "Buruh name cannot be blank")
    private String buruhName;
    
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Harvest ID cannot be blank")
    private String harvestId;
    
    @NotBlank(message = "Description cannot be blank")
    private String description;
}
