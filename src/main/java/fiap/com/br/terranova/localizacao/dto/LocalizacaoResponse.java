package fiap.com.br.terranova.localizacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalizacaoResponse {
    private Long id_localizacao;
    private BigDecimal loc_latitude;
    private BigDecimal loc_longitude;
}