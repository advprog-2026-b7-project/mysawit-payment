package id.ac.ui.cs.advprog.mysawit.payment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "wage_settings")
@Getter @Setter
public class WageSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buruh_wage_per_kg", nullable = false)
    private BigDecimal buruhWagePerKg;

    @Column(name = "supir_truck_wage_per_kg", nullable = false)
    private BigDecimal supirTruckWagePerKg;

    @Column(name = "mandor_wage_per_kg", nullable = false)
    private BigDecimal mandorWagePerKg;

    public WageSettings() {
        this.buruhWagePerKg = BigDecimal.ZERO;
        this.supirTruckWagePerKg = BigDecimal.ZERO;
        this.mandorWagePerKg = BigDecimal.ZERO;
    }

    public WageSettings(BigDecimal buruhWagePerKg, BigDecimal supirTruckWagePerKg,
                        BigDecimal mandorWagePerKg) {
        this.buruhWagePerKg = buruhWagePerKg;
        this.supirTruckWagePerKg = supirTruckWagePerKg;
        this.mandorWagePerKg = mandorWagePerKg;
    }
}
