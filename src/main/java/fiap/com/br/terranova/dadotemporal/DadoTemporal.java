package fiap.com.br.terranova.dadotemporal;

import fiap.com.br.terranova.reqapi.ReqApi;
import fiap.com.br.terranova.talhao.Talhao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dado_temporal")
public class DadoTemporal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dado")
    private Long idDado;

    @Column(name = "data_leitura", nullable = false)
    private LocalDate dataLeitura;

    @Column(name = "valor", nullable = false)
    private Double valor;

    @ManyToOne
    @JoinColumn(name = "talhao_id_talhao")
    private Talhao talhao;

    @ManyToOne
    @JoinColumn(name = "req_api_id_api")
    private ReqApi reqApi;
}
