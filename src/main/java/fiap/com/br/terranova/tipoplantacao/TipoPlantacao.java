package fiap.com.br.terranova.tipoplantacao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tipo_plantacao")
public class TipoPlantacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_plant")
    private Long idTipoPlant;

    @Column(name = "tipo_plant", length = 30, nullable = false)
    private String tipoPlant;
}