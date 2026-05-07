package id.ac.ui.cs.advprog.mysawit.payment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payrolls")
@Getter @Setter
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String workerId;
    private String workerName;
    private Double amount;
    private String status;
    private String referenceId;
    private LocalDateTime createdAt;
    private LocalDate tanggal;
    
    @Column(name = "payroll_type")
    private String payrollType;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    private LocalDateTime approvedAt;

    public Payroll() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.tanggal = LocalDate.now();
    }
}