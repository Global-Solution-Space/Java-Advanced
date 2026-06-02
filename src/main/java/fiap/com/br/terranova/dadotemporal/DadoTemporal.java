package fiap.com.br.terranova.dadotemporal;

import fiap.com.br.terranova.nasapower.NasaPower;
import fiap.com.br.terranova.satveg.SatVeg;
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
    @JoinColumn(name = "satveg_id_satveg")
    private SatVeg satveg;

    @ManyToOne
    @JoinColumn(name = "nasapower_id_nasapower")
    private NasaPower nasaPower;

    public static DadoTemporal criarParaSatVeg(String dataString, Double valor, SatVeg satveg) {
        return DadoTemporal.builder()
                .dataLeitura(LocalDate.parse(dataString))
                .valor(valor)
                .satveg(satveg)
                .build();
    }

    public static DadoTemporal criarParaNasaPower(LocalDate data, Double valor, NasaPower nasaPower) {
        return DadoTemporal.builder()
                .dataLeitura(data)
                .valor(valor)
                .nasaPower(nasaPower)
                .build();
    }
}
