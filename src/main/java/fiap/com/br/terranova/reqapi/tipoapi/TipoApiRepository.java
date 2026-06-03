package fiap.com.br.terranova.reqapi.tipoapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoApiRepository extends JpaRepository<TipoApi, Long> {
    Optional<TipoApi> findByTipoApi(String tipoApi);
}
