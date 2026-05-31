package fiap.com.br.terranova.satveg;

import fiap.com.br.terranova.talhao.Talhao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "satveg")
public class SatVeg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_satveg")
    private Long idSatveg;

    @Column(name = "tipo_perfil", nullable = false)
    private BigDecimal tipoPerfil;

    @Column(name = "satelite", nullable = false)
    private BigDecimal satelite;

    @Column(name = "pre_filtro", precision = 1)
    private Integer preFiltro;

    @Column(name = "filtro", length = 3)
    private String filtro;

    @Column(name = "parametro_filtro", precision = 2)
    private Integer parametroFiltro;

    @Column(name = "data_analise", nullable = false)
    private Timestamp dataAnalise;

    @ManyToOne
    @JoinColumn(name = "talhao_id_talhao")
    private Talhao talhao;
}