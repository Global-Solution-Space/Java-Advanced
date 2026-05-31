package fiap.com.br.terranova.satveg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SatVegRequest {
    private BigDecimal tipo_perfil;
    private BigDecimal satelite;
    private Integer pre_filtro;
    private String filtro;
    private Integer parametro_filtro;
    private String poligono;
    private Boolean todas_estatisticas;
    private Timestamp data_analise;
    private Long id_talhao;
}