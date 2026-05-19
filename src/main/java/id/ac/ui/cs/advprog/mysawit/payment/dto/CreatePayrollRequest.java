package id.ac.ui.cs.advprog.mysawit.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreatePayrollRequest {
    private String workerId;
    private String workerName;
    private Double amount;
    private String referenceId;
    private String payrollType;
    private String description;

    public CreatePayrollRequest() {
    }
}
