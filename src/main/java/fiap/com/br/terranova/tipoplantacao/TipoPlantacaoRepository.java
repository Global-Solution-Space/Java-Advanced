package fiap.com.br.terranova.tipoplantacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPlantacaoRepository extends JpaRepository<TipoPlantacao, Long> {
    boolean existsByTipoPlant(String tipoPlant);
}