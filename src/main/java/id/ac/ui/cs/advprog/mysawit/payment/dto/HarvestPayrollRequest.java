package id.ac.ui.cs.advprog.mysawit.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class HarvestPayrollRequest {
    private String buruhId;
    private String buruhName;
    private Double amount;
    private String harvestId;
    private String description;
}
