package fiap.com.br.terranova.propriedade;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {
    List<Propriedade> findByProdutorIdProdutor(Long idProdutor);
    boolean existsByLocalizacaoIdLocalizacao(Long idLocalizacao);
}
