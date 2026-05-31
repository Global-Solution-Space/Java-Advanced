package fiap.com.br.terranova.telefone;

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
@Table(name = "telefone")
public class Telefone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_telefone")
    private Long idTelefone;

    @Column(name = "ddd", length = 2, nullable = false)
    private String ddd;

    @Column(name = "numero", length = 9, nullable = false)
    private String numero;

    @ManyToOne
    @JoinColumn(name = "produtor_id_produtor")
    private Produtor produtor;
}
