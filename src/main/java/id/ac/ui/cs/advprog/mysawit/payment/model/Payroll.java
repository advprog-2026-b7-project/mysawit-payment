package id.ac.ui.cs.advprog.mysawit.payment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "payrolls")
@Getter @Setter
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String workerId;
    private Double amount;
    private String status;
    private String referenceId;

    public Payroll() {
        this.status = "PENDING";
    }
}