package fiap.com.br.terranova.reqapi;

import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "req_api")
public class ReqApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_api")
    private Long idApi;

    @Column(name = "tipo_param", length = 15, nullable = false)
    private String tipoParam;

    @Column(name = "data_analise", nullable = false)
    private Timestamp dataAnalise;

    @ManyToOne
    @JoinColumn(name = "tipo_api_id_tipo")
    private TipoApi tipoApi;

    @Builder.Default
    @OneToMany(mappedBy = "reqApi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DadoTemporal> dados = new ArrayList<>();
}
