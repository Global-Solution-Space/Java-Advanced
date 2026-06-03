package fiap.com.br.terranova.reqapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReqApiRepository extends JpaRepository<ReqApi, Long> {
}
