package fiap.com.br.terranova.satveg;

import fiap.com.br.terranova.talhao.Talhao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "satveg")
public class SatVeg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_satveg") //
    private Long id_satveg;

    @Column(name = "tipo_perfil", precision = 5, scale = 2)
    private BigDecimal tipo_perfil;

    @Column(name = "satelite", precision = 5, scale = 2)
    private BigDecimal satelite;

    @Column(name = "pre_filtro", precision = 1)
    private Integer pre_filtro;

    @Column(name = "filtro", length = 3)
    private String filtro;

    @Column(name = "parametro_filtro", precision = 2)
    private Integer parametro_filtro;

    @Lob
    @Column(name = "poligono")
    private String poligono;

    @Column(name = "todas_estatisticas")
    private Boolean todas_estatisticas;

    @Column(name = "data_analise")
    private Timestamp data_analise;

    @ManyToOne
    @JoinColumn(name = "id_talhao")
    private Talhao talhao;
}