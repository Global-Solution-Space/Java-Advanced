package fiap.com.br.terranova.nasapower;

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
@Table(name = "nasapower")
public class NasaPower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nasapower")
    private Long idNasapower;

    @Column(name = "data_inicio", length = 8, nullable = false)
    private String dataInicio;

    @Column(name = "data_fim", length = 8, nullable = false)
    private String dataFim;

    @Column(name = "elevacao", precision = 5, scale = 2, nullable = false)
    private BigDecimal elevacao;

    @Column(name = "data_analise", nullable = false)
    private Timestamp dataAnalise;

    @ManyToOne
    @JoinColumn(name = "talhao_id_talhao")
    private Talhao talhao;
}