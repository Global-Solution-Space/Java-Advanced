package fiap.com.br.terranova.propriedade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {
    List<Propriedade> findByProdutorIdProdutor(Long idProdutor);
    boolean existsByLocalizacaoIdLocalizacao(Long idLocalizacao);
}
