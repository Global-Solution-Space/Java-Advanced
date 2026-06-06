package fiap.com.br.terranova.talhao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TalhaoRepository extends JpaRepository<Talhao, Long> {
    List<Talhao> findByPropriedadeIdPropriedade(Long idPropriedade);
    List<Talhao> findByPropriedadeProdutorIdProdutor(Long idProdutor);
    boolean existsByLocalizacaoIdLocalizacao(Long idLocalizacao);
    boolean existsByTipoPlantacaoIdTipoPlant(Long idTipoPlant);
}