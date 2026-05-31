package fiap.com.br.terranova.tipoplantacao.dto;

import fiap.com.br.terranova.tipoplantacao.TipoPlantacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoPlantacaoRequest(
        @NotBlank
        @Size(max = 30)
        String tipoPlant
) {
    public TipoPlantacao toEntity() {
        return TipoPlantacao.builder()
                .tipoPlant(tipoPlant)
                .build();
    }
}