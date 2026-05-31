package fiap.com.br.terranova.nasapower;

import fiap.com.br.terranova.talhao.Talhao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nasapower")
public class NasaPower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_nasapower;

    @Column(name = "data_inicio", length = 8)
    private String data_inicio;

    @Column(name = "data_fim", length = 8)
    private String data_fim;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "elevacao", precision = 5, scale = 2)
    private BigDecimal elevacao;

    @ManyToOne
    @JoinColumn(name = "id_talhao")
    private Talhao talhao;
}