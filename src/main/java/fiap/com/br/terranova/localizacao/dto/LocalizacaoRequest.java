package fiap.com.br.terranova.localizacao.dto;

import fiap.com.br.terranova.localizacao.Localizacao;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LocalizacaoRequest(
        @NotNull
        @DecimalMin(value = "-33.75", message = "A latitude deve ser no mínimo -33.75 (território brasileiro)")
        @DecimalMax(value = "5.27", message = "A latitude deve ser no máximo 5.27 (território brasileiro)")
        BigDecimal locLatitude,

        @NotNull
        @DecimalMin(value = "-73.98", message = "A longitude deve ser no mínimo -73.98 (território brasileiro)")
        @DecimalMax(value = "-34.79", message = "A longitude deve ser no máximo -34.79 (território brasileiro)")
        BigDecimal locLongitude
) {
    public Localizacao toEntity() {
        return Localizacao.builder()
                .locLatitude(locLatitude)
                .locLongitude(locLongitude)
                .build();
    }
}