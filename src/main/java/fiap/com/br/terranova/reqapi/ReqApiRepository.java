package fiap.com.br.terranova.reqapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReqApiRepository extends JpaRepository<ReqApi, Long> {
    
    @Query("SELECT DISTINCT r FROM ReqApi r JOIN r.dados d WHERE d.talhao.idTalhao = :idTalhao")
    Page<ReqApi> findByTalhao_IdTalhao(@Param("idTalhao") Long idTalhao, Pageable pageable);
}
