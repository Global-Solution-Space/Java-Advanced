package fiap.com.br.terranova.dadotemporal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DadoTemporalRepository extends JpaRepository<DadoTemporal, Long> {
    List<DadoTemporal> findByTalhaoIdTalhao(Long idTalhao);
    List<DadoTemporal> findByReqApiIdApi(Long idApi);
    List<DadoTemporal> findByTalhaoIdTalhaoAndReqApiTipoApiTipoApiIgnoreCaseAndDataLeituraAfter(Long idTalhao, String tipoApi, LocalDate dataInicio);
}
