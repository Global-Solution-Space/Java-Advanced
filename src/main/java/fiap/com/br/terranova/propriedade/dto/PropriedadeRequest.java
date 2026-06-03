package fiap.com.br.terranova.propriedade.dto;

import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.propriedade.Propriedade;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PropriedadeRequest(
        @NotBlank
        @Size(max = 30)
        String nome,

        @NotNull
        @Positive(message = "O tamanho total deve ser maior que 0.")
        @DecimalMax(value = "10000.00", message = "O tamanho total nao pode exceder 10000.00 hectares.")
        Double tamanhoTotal,

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
