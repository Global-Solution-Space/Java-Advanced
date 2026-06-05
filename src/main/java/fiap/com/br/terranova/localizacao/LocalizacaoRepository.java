package fiap.com.br.terranova.localizacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {
    Optional<Localizacao> findByLocLatitudeAndLocLongitude(BigDecimal latitude, BigDecimal longitude);
}