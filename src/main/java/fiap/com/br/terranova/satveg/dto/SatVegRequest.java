package fiap.com.br.terranova.satveg.dto;

import fiap.com.br.terranova.satveg.SatVeg;
import fiap.com.br.terranova.talhao.Talhao;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

public record SatVegRequest(
        @NotNull
        Long idTalhao
) {
    public SatVeg toEntity(Talhao talhao) {
        return SatVeg.builder()
                .tipoPerfil("ndvi")
                .dataAnalise(new Timestamp(System.currentTimeMillis()))
                .talhao(talhao)
                .build();
    }
}