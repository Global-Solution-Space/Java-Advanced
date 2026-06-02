package fiap.com.br.terranova.nasapower.dto;

import fiap.com.br.terranova.nasapower.NasaPower;
import fiap.com.br.terranova.talhao.Talhao;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.time.LocalDate;

public record NasaPowerRequest(
        @NotNull
        LocalDate dataInicio,

        @NotNull
        LocalDate dataFim,

        @NotNull
        Long idTalhao
) {
    public NasaPower toEntity(Talhao talhao) {
        return NasaPower.builder()
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .parametro("PRECTOTCORR")
                .dataAnalise(new Timestamp(System.currentTimeMillis()))
                .talhao(talhao)
                .build();
    }
}