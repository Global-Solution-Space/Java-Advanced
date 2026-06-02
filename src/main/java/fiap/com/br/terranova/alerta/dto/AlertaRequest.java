package fiap.com.br.terranova.alerta.dto;

import fiap.com.br.terranova.alerta.Alerta;
import fiap.com.br.terranova.alerta.NivelAlerta;
import fiap.com.br.terranova.alerta.SimNao;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.validation.EnumValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;

public record AlertaRequest(
        @NotBlank
        @Size(max = 100)
        String titulo,

        @NotBlank
        @Size(max = 300)
        String descricao,

        @NotBlank
        @Size(max = 20)
        @EnumValidation(enumClass = NivelAlerta.class, message = "O nivel de alerta deve ser: BAIXO, MEDIO, ALTO ou CRITICO")
        String nivelAlerta,

        @NotBlank
        @Size(max = 1)
        @EnumValidation(enumClass = SimNao.class, message = "O campo resolvido deve ser 'S' ou 'N'")
        String resolvido,

        @NotNull
        Long idTalhao
) {
    public Alerta toEntity(Talhao talhao) {
        return Alerta.builder()
                .titulo(titulo)
                .descricao(descricao)
                .nivelAlerta(nivelAlerta)
                .resolvido(resolvido)
                .dataAlerta(new Timestamp(System.currentTimeMillis()))
                .talhao(talhao)
                .build();
    }
}