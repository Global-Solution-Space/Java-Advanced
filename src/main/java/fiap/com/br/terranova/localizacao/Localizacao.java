package fiap.com.br.terranova.localizacao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "localizacao")
public class Localizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_localizacao")
    private Long idLocalizacao;

    @Column(name = "loc_latitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal locLatitude;

    @Column(name = "loc_longitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal locLongitude;
}