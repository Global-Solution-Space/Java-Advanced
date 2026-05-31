package fiap.com.br.terranova.localizacao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "localizacao")
public class Localizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_localizacao")
    private Long id_localizacao;

    @Column(name = "loc_latitude", precision = 8, scale = 6)
    private BigDecimal loc_latitude;

    @Column(name = "loc_longitude", precision = 9, scale = 6)
    private BigDecimal loc_longitude;
}