package fiap.com.br.terranova.propriedade;

import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.produtor.Produtor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "propriedade")
public class Propriedade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_propriedade")
    private Long idPropriedade;

    @Column(name = "nome", length = 30, nullable = false)
    private String nome;

    @Column(name = "tamanho_total", nullable = false)
    private Double tamanhoTotal;

    @ManyToOne
    @JoinColumn(name = "produtor_id_produtor")
    private Produtor produtor;

    @OneToOne
    @JoinColumn(name = "localizacao_id_localizacao")
    private Localizacao localizacao;
}