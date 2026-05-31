package fiap.com.br.terranova.alerta.dto;

import fiap.com.br.terranova.alerta.Alerta;
import fiap.com.br.terranova.nasapower.NasaPower;
import fiap.com.br.terranova.satveg.SatVeg;
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
        String nivelAlerta,

        @NotBlank
        @Size(max = 1)
        String resolvido,

        @NotNull
        Long idSatveg,

        @NotNull
        Long idNasapower
) {
    public Alerta toEntity(SatVeg satVeg, NasaPower nasaPower) {
        return Alerta.builder()
                .titulo(titulo)
                .descricao(descricao)
                .nivelAlerta(nivelAlerta)
                .resolvido(resolvido)
                .dataAlerta(new Timestamp(System.currentTimeMillis()))
                .satVeg(satVeg)
                .nasaPower(nasaPower)
                .build();
    }
}