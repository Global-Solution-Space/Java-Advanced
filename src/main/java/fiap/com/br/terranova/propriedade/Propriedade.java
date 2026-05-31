package fiap.com.br.terranova.propriedade;

import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.produtor.Produtor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "propriedade")
public class Propriedade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_propriedade")
    private Long id_propriedade;

    @Column(name = "nome", length = 30)
    private String nome;

    @Column(name = "tamanho_total", precision = 10, scale = 2)
    private BigDecimal tamanhoTotal;

    @ManyToOne
    @JoinColumn(name = "id_produtor")
    private Produtor produtor;

    @ManyToOne
    @JoinColumn(name = "id_localizacao")
    private Localizacao localizacao;
}