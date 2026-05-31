package fiap.com.br.terranova.localizacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalizacaoRequest {
    private BigDecimal loc_latitude;
    private BigDecimal loc_longitude;
}