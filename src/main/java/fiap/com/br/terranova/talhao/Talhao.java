package fiap.com.br.terranova.talhao;

import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.propriedade.Propriedade;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "talhao")
public class Talhao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_talhao")
    private Long id_talhao;

    @Column(name = "nome_talhao", length = 30)
    private String nome_talhao;

    @Column(name = "volum_area", precision = 10, scale = 4)
    private BigDecimal volum_area;

    @ManyToOne
    @JoinColumn(name = "id_tipo_plant")
    private TipoPlantacao tipoPlantacao;

    @ManyToOne
    @JoinColumn(name = "id_propriedade")
    private Propriedade propriedade;

    @ManyToOne
    @JoinColumn(name = "id_localizacao")
    private Localizacao localizacao;
}