package fiap.com.br.terranova.tipoplantacao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tipo_plantacao")
public class TipoPlantacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_plant")
    private Long id_tipo_plant;

    @Column(name = "tipo_plant", length = 30)
    private String tipo_plant;
}