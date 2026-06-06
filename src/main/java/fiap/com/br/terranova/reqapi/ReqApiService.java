package fiap.com.br.terranova.reqapi;

import fiap.com.br.terranova.alerta.AlertaService;
import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.dadotemporal.DadoTemporalRepository;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.integration.DadoTemporalIntegrationService;
import fiap.com.br.terranova.reqapi.dto.ReqApiRequest;
import fiap.com.br.terranova.reqapi.dto.ReqApiResponse;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApiRepository;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReqApiService {

    private final ReqApiRepository reqApiRepository;
    private final TipoApiRepository tipoApiRepository;
    private final TalhaoRepository talhaoRepository;
    private final DadoTemporalRepository dadoTemporalRepository;
    private final AlertaService alertaService;
    private final DadoTemporalIntegrationService dadoTemporalIntegrationService;

    public Page<ReqApiResponse> findAll(Pageable pageable) {
        return reqApiRepository.findAll(pageable).map(ReqApiResponse::fromEntity);
    }

    public ReqApiResponse findById(Long id) {
        return ReqApiResponse.fromEntity(findReqApiById(id));
    }

    public Page<ReqApiResponse> findByTalhaoId(Long idTalhao, Pageable pageable) {
        return reqApiRepository.findByTalhao_IdTalhao(idTalhao, pageable)
                .map(ReqApiResponse::fromEntity);
    }

    @Transactional
    public ReqApiResponse create(ReqApiRequest request) {
        TipoApi tipoApi = getTipoApiByName(request.tipoApiNome());
        Talhao talhao = getTalhao(request.idTalhao());

        ReqApi entity = reqApiRepository.save(request.toEntity(tipoApi));

        List<DadoTemporal> dados = dadoTemporalIntegrationService.buscarDados(tipoApi, request, talhao, entity);
        dadoTemporalRepository.saveAll(dados);
        entity.setDados(dados);

        alertaService.analisarEGerarAlertas(talhao, tipoApi.getTipoApi());
        return ReqApiResponse.fromEntity(entity);
    }

    @Transactional
    public void delete(Long id) {
        ReqApi entity = findReqApiById(id);
        reqApiRepository.delete(entity);
    }

    private ReqApi findReqApiById(Long id) {
        return reqApiRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ReqApi com id " + id + " não encontrada."));
    }

    private TipoApi getTipoApiByName(String nome) {
        return tipoApiRepository.findByTipoApi(nome).orElseThrow(() -> new ResourceNotFoundException("TipoApi " + nome + " não encontrado."));
    }

    private Talhao getTalhao(Long id) {
        return talhaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Talhao com id " + id + " não encontrado."));
    }
}
