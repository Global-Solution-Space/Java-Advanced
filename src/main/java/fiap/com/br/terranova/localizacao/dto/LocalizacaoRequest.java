package fiap.com.br.terranova.localizacao.dto;

import fiap.com.br.terranova.localizacao.Localizacao;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LocalizacaoRequest(
        @NotNull
        BigDecimal locLatitude,

        @NotNull
        BigDecimal locLongitude
) {
    public Localizacao toEntity() {
        return Localizacao.builder()
                .locLatitude(locLatitude)
                .locLongitude(locLongitude)
                .build();
    }
}