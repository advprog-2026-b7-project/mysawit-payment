package id.ac.ui.cs.advprog.mysawit.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CalculationDetailsDTO {
    private BigDecimal wagePerKg;
    private BigDecimal kilogramProcessed;
    private BigDecimal baseAmount;
    private BigDecimal discountPercentage;
    private BigDecimal finalAmount;
    private String formula;

    public CalculationDetailsDTO() {
    }
}
