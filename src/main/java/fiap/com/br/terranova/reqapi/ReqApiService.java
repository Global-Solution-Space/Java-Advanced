package fiap.com.br.terranova.reqapi;

import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.dadotemporal.DadoTemporalRepository;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.integration.nasa.NasaPowerClient;
import fiap.com.br.terranova.integration.nasa.NasaPowerDataResponse;
import fiap.com.br.terranova.integration.satveg.SatVegClient;
import fiap.com.br.terranova.integration.satveg.SatVegDataRequest;
import fiap.com.br.terranova.integration.satveg.SatVegDataResponse;
import fiap.com.br.terranova.reqapi.dto.ReqApiRequest;
import fiap.com.br.terranova.reqapi.dto.ReqApiResponse;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReqApiService {

    private final ReqApiRepository reqApiRepository;
    private final TipoApiRepository tipoApiRepository;
    private final TalhaoRepository talhaoRepository;
    private final DadoTemporalRepository dadoTemporalRepository;
    private final NasaPowerClient nasaPowerClient;
    private final SatVegClient satVegClient;

    @Value("${satveg.api.token:Bearer e97dab05-eedc-39b9-a3fd-fa83cb5fef5e}")
    private String satVegToken;

    public Page<ReqApiResponse> findAll(Pageable pageable) {
        return reqApiRepository.findAll(pageable).map(ReqApiResponse::fromEntity);
    }

    public ReqApiResponse findById(Long id) {
        return ReqApiResponse.fromEntity(findReqApiById(id));
    }

    @Transactional
    public ReqApiResponse create(ReqApiRequest request) {
        TipoApi tipoApi = getTipoApiByName(request.tipoApiNome());
        Talhao talhao = getTalhao(request.idTalhao());

        ReqApi entity = request.toEntity(tipoApi);
        entity = reqApiRepository.save(entity);

        // Busca dados da API externa e persiste como dados temporais
        List<DadoTemporal> dados = fetchDadosExternos(tipoApi.getTipoApi(), request, talhao, entity);
        dadoTemporalRepository.saveAll(dados);
        entity.setDados(dados);

        return ReqApiResponse.fromEntity(entity);
    }

    @Transactional
    public ReqApiResponse update(Long id, ReqApiRequest request) {
        ReqApi existingEntity = findReqApiById(id);
        TipoApi tipoApi = getTipoApiByName(request.tipoApiNome());
        Talhao talhao = getTalhao(request.idTalhao());

        // Limpa dados temporais antigos
        dadoTemporalRepository.deleteAll(existingEntity.getDados());

        existingEntity.setTipoParam(request.tipoParam());
        existingEntity.setTipoApi(tipoApi);

        // Busca dados novos da API externa
        List<DadoTemporal> dados = fetchDadosExternos(tipoApi.getTipoApi(), request, talhao, existingEntity);
        dadoTemporalRepository.saveAll(dados);
        existingEntity.setDados(dados);

        return ReqApiResponse.fromEntity(reqApiRepository.save(existingEntity));
    }

    @Transactional
    public void delete(Long id) {
        ReqApi entity = findReqApiById(id);
        reqApiRepository.delete(entity);
    }

    private ReqApi findReqApiById(Long id) {
        return reqApiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReqApi com id " + id + " nao encontrada."));
    }

    private TipoApi getTipoApiByName(String nome) {
        return tipoApiRepository.findByTipoApi(nome)
                .orElseThrow(() -> new ResourceNotFoundException("TipoApi " + nome + " nao encontrado."));
    }

    private Talhao getTalhao(Long id) {
        return talhaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Talhao com id " + id + " nao encontrado."));
    }

    // INTEGRAÇÃO EXTERNA

    private List<DadoTemporal> fetchDadosExternos(String tipoApiNome, ReqApiRequest request, Talhao talhao, ReqApi reqApi) {
        String tipo = tipoApiNome.toUpperCase();
        return switch (tipo) {
            case "NASAPOWER" -> fetchNasaPowerData(talhao, reqApi);
            case "SATVEG" -> fetchSatVegData(talhao, reqApi);
            default -> throw new IllegalArgumentException("Tipo de API nao suportado: " + tipo);
        };
    }

    private List<DadoTemporal> fetchNasaPowerData(Talhao talhao, ReqApi reqApi) {
        try {
            log.info("Buscando dados climaticos da NASA para o Talhao {}", talhao.getIdTalhao());
            LocalDate dataInicio = LocalDate.of(2020, 1, 1);
            LocalDate dataFim = LocalDate.now();
            NasaPowerDataResponse apiResponse = nasaPowerClient.getDailyData(buildNasaQuery(dataInicio, dataFim, talhao));

            Map<String, Double> dadosBrutos = apiResponse.properties().parameter().get("PRECTOTCORR");
            DateTimeFormatter nasaFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            List<DadoTemporal> dados = new ArrayList<>();

            dadosBrutos.forEach((dataAntiga, valor) -> {
                if (valor != null && valor > -900.0) {
                    LocalDate dataLeitura = LocalDate.parse(dataAntiga, nasaFormatter);
                    dados.add(DadoTemporal.builder()
                            .dataLeitura(dataLeitura)
                            .valor(valor)
                            .talhao(talhao)
                            .reqApi(reqApi)
                            .build());
                }
            });

            log.info("NASA POWER retornou {} registros para o Talhao {}", dados.size(), talhao.getIdTalhao());
            return dados;

        } catch (feign.FeignException.BadRequest e) {
            log.error("Erro 400 ao integrar com a API da NASA", e);
            throw new IllegalArgumentException("Erro na integracao com NASA POWER: " + extractDetail(e.contentUTF8()));
        } catch (feign.FeignException e) {
            log.error("Erro na API da NASA", e);
            throw new IllegalArgumentException("Falha na integracao com NASA POWER. Status: " + e.status());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao integrar com a API da NASA.", e);
            throw new IllegalArgumentException("Erro inesperado na integracao com a NASA POWER: " + e.getMessage());
        }
    }

    private List<DadoTemporal> fetchSatVegData(Talhao talhao, ReqApi reqApi) {
        try {
            log.info("Buscando series temporais na Embrapa SATveg para o Talhao {}", talhao.getIdTalhao());
            SatVegDataResponse apiResponse = satVegClient.getSeries(satVegToken, buildSatVegRequest(talhao));

            List<DadoTemporal> dados = new ArrayList<>();
            for (int i = 0; i < apiResponse.listaSerie().size(); i++) {
                String dataString = apiResponse.listaDatas().get(i);
                if (dataString != null && dataString.compareTo("2020-01-01") >= 0) {
                    dados.add(DadoTemporal.builder()
                            .dataLeitura(LocalDate.parse(dataString))
                            .valor(apiResponse.listaSerie().get(i))
                            .talhao(talhao)
                            .reqApi(reqApi)
                            .build());
                }
            }

            log.info("SATveg retornou {} pontos filtrados para o Talhao {}", dados.size(), talhao.getIdTalhao());
            return dados;

        } catch (feign.FeignException.BadRequest e) {
            log.error("Erro 400 ao integrar com a API da Embrapa SATveg", e);
            throw new IllegalArgumentException("Erro na integracao com SATveg: " + extractDetail(e.contentUTF8()));
        } catch (feign.FeignException e) {
            log.error("Erro na API da Embrapa SATveg", e);
            throw new IllegalArgumentException("Falha na integracao com SATveg. Status: " + e.status());
        } catch (Exception e) {
            log.error("Erro ao integrar com a API da Embrapa SATveg", e);
            throw new IllegalArgumentException("Erro inesperado na integracao com a Embrapa SATveg: " + e.getMessage());
        }
    }

    // BUILDERS

    private String extractDetail(String json) {
        try {
            if (json != null && json.contains("\"detail\":\"")) {
                int start = json.indexOf("\"detail\":\"") + 10;
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
        } catch (Exception ignored) {}
        return "Coordenadas ou parametros invalidos.";
    }

    private Map<String, Object> buildNasaQuery(LocalDate dataInicio, LocalDate dataFim, Talhao talhao) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Map.ofEntries(
                Map.entry("start", dataInicio.format(formatter)),
                Map.entry("end", dataFim.format(formatter)),
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

    private SatVegDataRequest buildSatVegRequest(Talhao talhao) {
        return SatVegDataRequest.builder()
                .tipoPerfil("NVDI")
                .satelite("comb")
                .preFiltro(3)
                .filtro("sav")
                .parametroFiltro(4)
                .latitude(talhao.getLocalizacao().getLocLatitude())
                .longitude(talhao.getLocalizacao().getLocLongitude())
                .build();
    }
}
