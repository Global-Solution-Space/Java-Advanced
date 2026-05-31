package fiap.com.br.terranova.alerta;

import fiap.com.br.terranova.nasapower.NasaPower;
import fiap.com.br.terranova.satveg.SatVeg;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "alerta_agricola")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long idAlerta;

    @Column(name = "titulo", length = 100, nullable = false)
    private String titulo;

    @Column(name = "descricao", length = 300, nullable = false)
    private String descricao;

    @Column(name = "nivel_alerta", length = 20, nullable = false)
    private String nivelAlerta;

    @Column(name = "resolvido", length = 1, nullable = false)
    private String resolvido;

    @Column(name = "data_alerta", nullable = false)
    private Timestamp dataAlerta;

    @ManyToOne
    @JoinColumn(name = "satveg_id_satveg")
    private SatVeg satVeg;

    @ManyToOne
    @JoinColumn(name = "nasapower_id_nasapower")
    private NasaPower nasaPower;
}
