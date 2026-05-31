package fiap.com.br.terranova.nasapower.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NasaPowerRequest {
    private String data_inicio;
    private String data_fim;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal elevacao;
    private Long id_talhao;
}