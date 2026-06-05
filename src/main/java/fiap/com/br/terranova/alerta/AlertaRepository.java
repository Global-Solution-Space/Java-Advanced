package fiap.com.br.terranova.alerta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fiap.com.br.terranova.talhao.Talhao;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByTalhaoPropriedadeProdutorIdProdutor(Long idProdutor);
    boolean existsByTalhaoAndTituloAndResolvido(Talhao talhao, String titulo, String resolvido);
}