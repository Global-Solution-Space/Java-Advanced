package fiap.com.br.terranova.telefone.dto;

import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.telefone.Telefone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TelefoneRequest(
        @NotBlank
        @Size(min = 2, max = 2)
        String ddd,

        @NotBlank
        @Size(max = 9)
        String numero,

        @NotNull
        Long idProdutor
) {
    public Telefone toEntity(Produtor produtor) {
        return Telefone.builder()
                .ddd(ddd)
                .numero(numero)
                .produtor(produtor)
                .build();
    }
}
