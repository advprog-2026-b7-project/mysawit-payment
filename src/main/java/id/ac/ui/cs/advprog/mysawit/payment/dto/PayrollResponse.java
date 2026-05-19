package id.ac.ui.cs.advprog.mysawit.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PayrollResponse {
    private UUID id;
    private String workerId;
    private String workerName;
    private Double amount;
    private String status;
    private String referenceId;
    private LocalDate tanggal;
    private String payrollType;
    private String description;
    private LocalDateTime createdAt;
    private CalculationDetailsDTO calculationDetails;

    public PayrollResponse() {
    }

    public PayrollResponse(UUID id, String workerId, String workerName, Double amount,
                          String status, String referenceId, LocalDate tanggal,
                          String payrollType, String description, LocalDateTime createdAt) {
        this.id = id;
        this.workerId = workerId;
        this.workerName = workerName;
        this.amount = amount;
        this.status = status;
        this.referenceId = referenceId;
        this.tanggal = tanggal;
        this.payrollType = payrollType;
        this.description = description;
        this.createdAt = createdAt;
        this.calculationDetails = null;
    }
}
