package fiap.com.br.terranova.localizacao.dto;

import fiap.com.br.terranova.localizacao.Localizacao;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LocalizacaoRequest(
        @NotNull
        @DecimalMin(value = "-34.00", message = "A latitude deve ser no mínimo -34.00")
        @DecimalMax(value = "6.00", message = "A latitude deve ser no máximo 6.00")
        BigDecimal locLatitude,

        @NotNull
        @DecimalMin(value = "-74.00", message = "A longitude deve ser no mínimo -74.00")
        @DecimalMax(value = "-28.00", message = "A longitude deve ser no máximo -28.00")
        BigDecimal locLongitude
) {
    public Localizacao toEntity() {
        return Localizacao.builder()
                .locLatitude(locLatitude)
                .locLongitude(locLongitude)
                .build();
    }
}