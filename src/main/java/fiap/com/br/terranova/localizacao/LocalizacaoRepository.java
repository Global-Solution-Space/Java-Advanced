package fiap.com.br.terranova.localizacao;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {
    Optional<Localizacao> findByCoordenadas(Point coordenadas);
}