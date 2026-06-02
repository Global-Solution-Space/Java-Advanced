package fiap.com.br.terranova.nasapower;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NasaPowerService {

    private final NasaPowerRepository repository;
    private final TalhaoRepository talhaoRepository;
    private final NasaPowerClient nasaPowerClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        fetchAndSetNasaPowerData(request, entity, "{}");

        return NasaPowerResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public NasaPowerResponse update(Long id, NasaPowerRequest request) {
        NasaPower existingEntity = findNasaPowerById(id);
        NasaPower entity = request.toEntity(getTalhao(request.idTalhao()));
        entity.setIdNasapower(id);
        entity.setDataAnalise(existingEntity.getDataAnalise());
        
        fetchAndSetNasaPowerData(request, entity, existingEntity.getDadosJson());

        return NasaPowerResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        NasaPower entity = findNasaPowerById(id);
        repository.delete(entity);
    }

    private NasaPower findNasaPowerById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("NasaPower com id " + id + " nao encontrado."));
    }

    private Talhao getTalhao(Long id) {
        return talhaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Talhao com id " + id + " nao encontrado."));
    }

    private void fetchAndSetNasaPowerData(NasaPowerRequest request, NasaPower entity, String fallbackJson) {
        try {
            log.info("Buscando dados climaticos da NASA para o Talhao {}", entity.getTalhao().getIdTalhao());
            NasaPowerDataResponse apiResponse = nasaPowerClient.getDailyData(buildApiQuery(request, entity.getTalhao()));
            
            // Vai direto ao ponto (NullPointerException é capturado pelo catch se a API falhar estruturalmente)
            Map<String, Double> dadosBrutos = apiResponse.properties().parameter().get("PRECTOTCORR");
            Map<String, Double> datasNormalizadas = new LinkedHashMap<>();
            
            dadosBrutos.forEach((dataAntiga, valor) -> {
                // Ignora o fill_value da NASA (-999)
                if (valor != null && valor > -900.0) {
                    String dataFormatada = (dataAntiga != null && dataAntiga.length() == 8) 
                            ? dataAntiga.substring(0, 4) + "-" + dataAntiga.substring(4, 6) + "-" + dataAntiga.substring(6, 8)
                            : dataAntiga;
                    datasNormalizadas.put(dataFormatada, valor);
                }
            });
            
            // Monta o dicionário final padronizado
            Map<String, Object> jsonFinal = Map.of("PRECTOTCORR", datasNormalizadas);
            entity.setDadosJson(objectMapper.writeValueAsString(jsonFinal));
            log.info("Dados climaticos da NASA mapeados e filtrados com sucesso.");
            
        } catch (Exception e) {
            log.error("Erro ao integrar com a API da NASA ou dados estruturais ausentes.", e);
            entity.setDadosJson(fallbackJson);
        }
    }

    private Map<String, Object> buildApiQuery(NasaPowerRequest request, Talhao talhao) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Map.ofEntries(
                Map.entry("start", request.dataInicio().format(formatter)),
                Map.entry("end", request.dataFim().format(formatter)),
                Map.entry("latitude", talhao.getLocalizacao().getLocLatitude()),
                Map.entry("longitude", talhao.getLocalizacao().getLocLongitude()),
                Map.entry("community", "ag"),
                Map.entry("parameters", "PRECTOTCORR"),
                Map.entry("format", "JSON"),
                Map.entry("units", "metric"),
                Map.entry("user", "terranova"),
                Map.entry("header", true),
                Map.entry("time-standard", "UTC")
        );
    }
}