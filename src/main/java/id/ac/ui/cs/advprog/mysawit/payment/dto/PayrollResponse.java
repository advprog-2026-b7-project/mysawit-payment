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

    public PayrollResponse() {
    }
}
