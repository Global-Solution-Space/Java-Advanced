package fiap.com.br.terranova.reqapi.tipoapi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoApiRepository extends JpaRepository<TipoApi, Long> {
    Optional<TipoApi> findByTipoApi(String tipoApi);
}
