package fiap.com.br.terranova.nasapower;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.integration.nasa.NasaPowerClient;
import fiap.com.br.terranova.integration.nasa.NasaPowerDataResponse;
import fiap.com.br.terranova.nasapower.dto.NasaPowerRequest;
import fiap.com.br.terranova.nasapower.dto.NasaPowerResponse;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NasaPowerService {

    private final NasaPowerRepository repository;
    private final TalhaoRepository talhaoRepository;
    private final NasaPowerClient nasaPowerClient;

    public Page<NasaPowerResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(NasaPowerResponse::fromEntity);
    }

    public NasaPowerResponse findById(Long id) {
        return NasaPowerResponse.fromEntity(findNasaPowerById(id));
    }

    @Transactional
    public NasaPowerResponse create(NasaPowerRequest request) {
        Talhao talhao = getTalhao(request.idTalhao());
        NasaPower entity = request.toEntity(talhao);

        try {
            log.info("Buscando dados climaticos da NASA para o Talhao {}", talhao.getIdTalhao());
            NasaPowerDataResponse apiResponse = nasaPowerClient.getDailyData(buildApiQuery(request, talhao));

            BigDecimal elevacaoApi = BigDecimal.valueOf(apiResponse.geometry().coordinates().get(2));
            entity.setElevacao(elevacaoApi);
            log.info("Elevacao de {} recebida com sucesso da NASA.", elevacaoApi);

        } catch (Exception e) {
            log.error("Erro ao integrar com a API da NASA. Usando dados de fallback do Request.", e);
        }

        return NasaPowerResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public NasaPowerResponse update(Long id, NasaPowerRequest request) {
        NasaPower existingEntity = findNasaPowerById(id);
        NasaPower entity = request.toEntity(getTalhao(request.idTalhao()));
        entity.setIdNasapower(id);
        entity.setElevacao(existingEntity.getElevacao());
        entity.setDataAnalise(existingEntity.getDataAnalise());
        return NasaPowerResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        NasaPower entity = findNasaPowerById(id);
        repository.delete(entity);
    }

    private NasaPower findNasaPowerById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NasaPower com id " + id + " nao encontrado."));
    }

    private Talhao getTalhao(Long id) {
        return talhaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Talhao com id " + id + " nao encontrado."));
    }

    private Map<String, Object> buildApiQuery(NasaPowerRequest request, Talhao talhao) {
        return Map.ofEntries(
                Map.entry("start", request.dataInicio()),
                Map.entry("end", request.dataFim()),
                Map.entry("latitude", talhao.getLocalizacao().getLocLatitude()),
                Map.entry("longitude", talhao.getLocalizacao().getLocLongitude()),
                Map.entry("community", "ag"),
                Map.entry("parameters", "PRECTOTCORR,T2M"),
                Map.entry("format", "JSON"),
                Map.entry("units", "metric"),
                Map.entry("user", "terranova"),
                Map.entry("header", true),
                Map.entry("time-standard", "UTC")
        );
    }
}