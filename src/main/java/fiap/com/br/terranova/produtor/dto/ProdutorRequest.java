package fiap.com.br.terranova.produtor.dto;

import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.telefone.dto.TelefoneRequest;
import fiap.com.br.terranova.validation.UniqueEmail;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProdutorRequest(
        @NotBlank
        @Size(max = 30)
        String nome,

        @NotBlank
        @Email
        @UniqueEmail
        @Size(max = 30)
        String email,

        @NotBlank
        @Size(min = 6, max = 30)
        String senha,

        @Valid
        TelefoneRequest telefone
) {
    public Produtor toEntity() {
        return Produtor.builder()
                .nome(nome)
                .email(email)
                .senha(senha)
                .build();
    }
}
