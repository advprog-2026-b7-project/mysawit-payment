package id.ac.ui.cs.advprog.mysawit.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollApprovalRequest {
    @NotBlank(message = "Action cannot be blank")
    private String action; 
    
    private String reason;
}
