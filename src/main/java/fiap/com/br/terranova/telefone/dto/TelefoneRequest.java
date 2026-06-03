package fiap.com.br.terranova.telefone.dto;

import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.telefone.Telefone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TelefoneRequest(
        @NotBlank
        @Pattern(regexp = "\\d{2}", message = "O DDD deve conter exatamente 2 digitos numericos")
        String ddd,

        @NotBlank
        @Pattern(regexp = "\\d{8,9}", message = "O numero deve conter de 8 a 9 digitos numericos")
        String numero,

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
