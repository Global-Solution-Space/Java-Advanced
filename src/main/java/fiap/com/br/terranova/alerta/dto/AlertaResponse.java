package fiap.com.br.terranova.alerta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertaResponse {
    private Long id_alerta;
    private String titulo;
    private String descricao;
    private String nivel_alerta;
    private Boolean resolvido;
    private Timestamp data_alerta;
    private Long id_nasapower;
    private Long id_satveg;
}