package fiap.com.br.terranova.produtor;

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
@Table(name = "produtor")
public class Produtor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produtor")
    private Long idProdutor;

    @Column(name = "nome", length = 30, nullable = false)
    private String nome;

    @Column(name = "email", length = 30, nullable = false)
    private String email;

    @Column(name = "senha", length = 30, nullable = false)
    private String senha;
}
