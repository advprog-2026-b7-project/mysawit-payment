package id.ac.ui.cs.advprog.mysawit.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class WageSettingsRequest {
    @NotNull(message = "Upah Buruh per Kg harus diisi")
    @Positive(message = "Upah Buruh per Kg harus lebih besar dari 0")
    private BigDecimal buruhWagePerKg;

    @NotNull(message = "Upah Supir Truck per Kg harus diisi")
    @Positive(message = "Upah Supir Truck per Kg harus lebih besar dari 0")
    private BigDecimal supirTruckWagePerKg;

    @NotNull(message = "Upah Mandor per Kg harus diisi")
    @Positive(message = "Upah Mandor per Kg harus lebih besar dari 0")
    private BigDecimal mandorWagePerKg;

    public WageSettingsRequest() {
    }
}
