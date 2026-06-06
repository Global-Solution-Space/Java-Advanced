package fiap.com.br.terranova.dadotemporal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DadoTemporalRepository extends JpaRepository<DadoTemporal, Long> {
    Page<DadoTemporal> findByTalhaoIdTalhao(Long idTalhao, Pageable pageable);
    List<DadoTemporal> findAllByTalhaoIdTalhao(Long idTalhao);
    Page<DadoTemporal> findByReqApiIdApi(Long idApi, Pageable pageable);

    @Query("""
            SELECT d FROM DadoTemporal d
            WHERE d.talhao.idTalhao = :idTalhao
              AND LOWER(d.reqApi.tipoApi.tipoApi) = LOWER(:tipoApi)
              AND d.dataLeitura > :dataInicio
            ORDER BY d.dataLeitura DESC
            """)
    List<DadoTemporal> buscarDadosParaAnalise(
            @Param("idTalhao") Long idTalhao,
            @Param("tipoApi") String tipoApi,
            @Param("dataInicio") LocalDate dataInicio
    );
}
