package id.ac.ui.cs.advprog.mysawit.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class WageSettingsResponse {
    private Long id;
    private BigDecimal buruhWagePerKg;
    private BigDecimal supirTruckWagePerKg;
    private BigDecimal mandorWagePerKg;

    public WageSettingsResponse() {
    }
}
