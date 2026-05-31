package fiap.com.br.terranova.produtor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "produtor")
public class Produtor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_produtor;

    @Column(name = "nome", length = 30)
    private String nome;
    private String email;
    private String senha;
}
