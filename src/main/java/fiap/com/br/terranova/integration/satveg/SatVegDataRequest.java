package fiap.com.br.terranova.integration.satveg;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SatVegDataRequest {
    private String tipoPerfil;
    private String satelite;
    private Integer preFiltro;
    private String filtro;
    private Integer parametroFiltro;
    private BigDecimal longitude;
    private BigDecimal latitude;
}
