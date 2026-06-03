package fiap.com.br.terranova.talhao;

import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.propriedade.Propriedade;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacao;
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
@Table(name = "talhao")
public class Talhao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_talhao")
    private Long idTalhao;

    @Column(name = "nome_talhao", length = 30, nullable = false)
    private String nomeTalhao;

    @Column(name = "volum_area", nullable = false)
    private Double volumArea;

    @ManyToOne
    @JoinColumn(name = "tipo_plantacao_id_tipo_plant")
    private TipoPlantacao tipoPlantacao;

    @ManyToOne
    @JoinColumn(name = "propriedade_id_propriedade")
    private Propriedade propriedade;

    @OneToOne
    @JoinColumn(name = "localizacao_id_localizacao")
    private Localizacao localizacao;
}