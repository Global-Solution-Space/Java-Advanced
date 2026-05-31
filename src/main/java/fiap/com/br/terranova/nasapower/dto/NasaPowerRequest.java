package fiap.com.br.terranova.nasapower.dto;

import fiap.com.br.terranova.nasapower.NasaPower;
import fiap.com.br.terranova.talhao.Talhao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record NasaPowerRequest(
        @NotBlank
        @Size(min = 8, max = 8)
        @Pattern(regexp = "\\d{8}", message = "A data deve estar no formato YYYYMMDD")
        String dataInicio,

        @NotBlank
        @Size(min = 8, max = 8)
        @Pattern(regexp = "\\d{8}", message = "A data deve estar no formato YYYYMMDD")
        String dataFim,

        @NotNull
        BigDecimal elevacao,

        @NotNull
        Long idTalhao
) {
    public NasaPower toEntity(Talhao talhao) {
        return NasaPower.builder()
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .elevacao(elevacao)
                .dataAnalise(new Timestamp(System.currentTimeMillis()))
                .talhao(talhao)
                .build();
    }
}