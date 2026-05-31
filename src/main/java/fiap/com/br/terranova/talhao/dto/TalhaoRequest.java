package fiap.com.br.terranova.talhao.dto;

import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.propriedade.Propriedade;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TalhaoRequest(
        @NotBlank
        @Size(max = 30)
        String nomeTalhao,

        @NotNull
        BigDecimal volumArea,

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
