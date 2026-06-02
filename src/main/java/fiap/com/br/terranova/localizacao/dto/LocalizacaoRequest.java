package fiap.com.br.terranova.localizacao.dto;

import fiap.com.br.terranova.localizacao.Localizacao;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LocalizacaoRequest(
        @NotNull
        @DecimalMin(value = "-90.0", message = "A latitude deve ser no minimo -90.0")
        @DecimalMax(value = "90.0", message = "A latitude deve ser no maximo 90.0")
        BigDecimal locLatitude,

        @NotNull
        @DecimalMin(value = "-180.0", message = "A longitude deve ser no minimo -180.0")
        @DecimalMax(value = "180.0", message = "A longitude deve ser no maximo 180.0")
        BigDecimal locLongitude
) {
    public Localizacao toEntity() {
        return Localizacao.builder()
                .locLatitude(locLatitude)
                .locLongitude(locLongitude)
                .build();
    }
}