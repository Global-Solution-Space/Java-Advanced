package fiap.com.br.terranova.talhao.dto;

import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.propriedade.Propriedade;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacao;
import fiap.com.br.terranova.validation.ValidTalhaoArea;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@ValidTalhaoArea
public record TalhaoRequest(
        @NotBlank
        @Size(max = 30)
        String nomeTalhao,

        @NotNull
        @Positive(message = "A area/volume do talhao deve ser maior que 0.")
        @DecimalMax(value = "1000.00", message = "A area/volume do talhao não pode exceder 1000.00 hectares.")
        Double volumArea,

        @NotNull
        Long idTipoPlantacao,

        @NotNull
        Long idPropriedade,

        @NotNull
        Long idLocalizacao
) {
    public Talhao toEntity(TipoPlantacao tipoPlantacao, Propriedade propriedade, Localizacao localizacao) {
        return Talhao.builder()
                .nomeTalhao(nomeTalhao)
                .volumArea(volumArea)
                .tipoPlantacao(tipoPlantacao)
                .propriedade(propriedade)
                .localizacao(localizacao)
                .build();
    }
}
