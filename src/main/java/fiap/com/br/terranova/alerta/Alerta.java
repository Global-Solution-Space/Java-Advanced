package fiap.com.br.terranova.alerta;

import fiap.com.br.terranova.nasapower.NasaPower;
import fiap.com.br.terranova.satveg.SatVeg;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "alerta_agricola")
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_alerta;

    @Column(name = "titulo", length = 100)
    private String titulo;

    @Column(name = "descricao", length = 300)
    private String descricao;

    @Column(name = "nivel_alerta", length = 20)
    private String nivel_alerta;

    @Column(name = "resolvido", length = 1)
    private Boolean resolvido;

    @Column(name = "data_alerta")
    private Timestamp data_alerta;

    @ManyToOne
    @JoinColumn(name = "id_nasapower")
    private NasaPower nasaPower;

    @ManyToOne
    @JoinColumn(name = "id_satveg")
    private SatVeg satVeg;
}
