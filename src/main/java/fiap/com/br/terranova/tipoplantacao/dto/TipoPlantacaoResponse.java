package fiap.com.br.terranova.tipoplantacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoPlantacaoResponse {
    private Long id_tipo_plant;
    private String tipo_plant;
}