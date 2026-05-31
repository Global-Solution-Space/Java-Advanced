package fiap.com.br.terranova.satveg.dto;

import fiap.com.br.terranova.satveg.SatVeg;
import fiap.com.br.terranova.talhao.Talhao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record SatVegRequest(
        @NotNull
        BigDecimal tipoPerfil,

        @NotNull
        BigDecimal satelite,

        Integer preFiltro,

        @Size(max = 3)
        String filtro,

        Integer parametroFiltro,

        @NotNull
        Long idTalhao
) {
    public SatVeg toEntity(Talhao talhao) {
        return SatVeg.builder()
                .tipoPerfil(tipoPerfil)
                .satelite(satelite)
                .preFiltro(preFiltro)
                .filtro(filtro)
                .parametroFiltro(parametroFiltro)
                .dataAnalise(new Timestamp(System.currentTimeMillis()))
                .talhao(talhao)
                .build();
    }
}