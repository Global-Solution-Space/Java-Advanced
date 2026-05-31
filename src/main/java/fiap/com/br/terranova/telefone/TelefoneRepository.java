package fiap.com.br.terranova.telefone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
    Optional<Telefone> findByProdutorIdProdutor(Long idProdutor);
}
