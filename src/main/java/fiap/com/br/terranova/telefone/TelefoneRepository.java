package fiap.com.br.terranova.telefone;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
    Optional<Telefone> findByProdutorIdProdutor(Long idProdutor);
    List<Telefone> findAllByProdutorIdProdutor(Long idProdutor);
    boolean existsByDddAndNumero(String ddd, String numero);
}
