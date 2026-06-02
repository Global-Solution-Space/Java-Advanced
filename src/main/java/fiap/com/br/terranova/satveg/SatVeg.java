package fiap.com.br.terranova.satveg;

import fiap.com.br.terranova.talhao.Talhao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import fiap.com.br.terranova.dadotemporal.DadoTemporal;

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

    @Column(name = "tipo_perfil", length = 4, nullable = false)
    private String tipoPerfil;


    @Column(name = "data_analise", nullable = false)
    private Timestamp dataAnalise;

    @ManyToOne
    @JoinColumn(name = "talhao_id_talhao")
    private Talhao talhao;

    @OneToMany(mappedBy = "satveg", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DadoTemporal> dados = new ArrayList<>();
}