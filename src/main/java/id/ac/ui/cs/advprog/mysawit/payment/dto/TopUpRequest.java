package id.ac.ui.cs.advprog.mysawit.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TopUpRequest {

    @NotNull(message = "Jumlah SawitDollar tidak boleh kosong")
    @Min(value = 1, message = "Minimal top-up adalah 1 SawitDollar")
    private Double amountSawitDollar;
}