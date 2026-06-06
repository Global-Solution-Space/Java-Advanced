package fiap.com.br.terranova.reqapi;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReqApiRepository extends JpaRepository<ReqApi, Long> {
    
    @Query("SELECT DISTINCT r FROM ReqApi r JOIN r.dados d WHERE d.talhao.idTalhao = :idTalhao")
    List<ReqApi> findByTalhao_IdTalhao(@Param("idTalhao") Long idTalhao);
}
