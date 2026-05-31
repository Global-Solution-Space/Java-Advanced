package fiap.com.br.terranova.propriedade.dto;

import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.propriedade.Propriedade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PropriedadeRequest(
        @NotBlank
        @Size(max = 30)
        String nome,

        @NotNull
        BigDecimal tamanhoTotal,

        @NotNull
        Long idProdutor,

        @NotNull
        Long idLocalizacao
) {
    public Propriedade toEntity(Produtor produtor, Localizacao localizacao) {
        return Propriedade.builder()
                .nome(nome)
                .tamanhoTotal(tamanhoTotal)
                .produtor(produtor)
                .localizacao(localizacao)
                .build();
    }
}
