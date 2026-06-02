package fiap.com.br.terranova.nasapower;

import fiap.com.br.terranova.talhao.Talhao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nasapower")
public class NasaPower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nasapower")
    private Long idNasapower;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "parametro", length = 30, nullable = false)
    private String parametro;

    @Column(name = "data_analise", nullable = false)
    private Timestamp dataAnalise;

    @Lob
    @Column(name = "dados_json", nullable = false)
    private String dadosJson;

    @ManyToOne
    @JoinColumn(name = "talhao_id_talhao")
    private Talhao talhao;
}