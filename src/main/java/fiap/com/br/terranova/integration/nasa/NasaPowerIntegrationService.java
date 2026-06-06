package fiap.com.br.terranova.integration.nasa;

import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.reqapi.ReqApi;
import fiap.com.br.terranova.talhao.Talhao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fiap.com.br.terranova.integration.FeignErrorUtil.extractDetail;

@Slf4j
@Service
@RequiredArgsConstructor
public class NasaPowerIntegrationService {

    private static final String PARAMETRO_PRECIPITACAO = "prectotcorr";
    private static final DateTimeFormatter NASA_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final NasaPowerClient nasaPowerClient;

    public List<DadoTemporal> buscarDados(Talhao talhao, ReqApi reqApi) {
        try {
            log.info("Buscando dados climaticos da NASA para o Talhao {}", talhao.getIdTalhao());

            NasaPowerDataResponse apiResponse = nasaPowerClient.getDailyData(buildQuery(LocalDate.now(), talhao));
            Map<String, Double> dadosBrutos = getDadosBrutos(apiResponse);
            List<DadoTemporal> dados = new ArrayList<>();

            dadosBrutos.forEach((dataAntiga, valor) -> {
                if (valor != null && valor > -900.0) {
                    dados.add(DadoTemporal.builder()
                            .dataLeitura(LocalDate.parse(dataAntiga, NASA_FORMATTER))
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

    private Map<String, Object> buildQuery(LocalDate dataFim, Talhao talhao) {
        return Map.ofEntries(
                Map.entry("start", LocalDate.of(2020, 1, 1).format(NASA_FORMATTER)),
                Map.entry("end", dataFim.format(NASA_FORMATTER)),
                Map.entry("latitude", talhao.getLocalizacao().getLocLatitude()),
                Map.entry("longitude", talhao.getLocalizacao().getLocLongitude()),
                Map.entry("community", "ag"),
                Map.entry("parameters", PARAMETRO_PRECIPITACAO),
                Map.entry("format", "JSON"),
                Map.entry("units", "metric"),
                Map.entry("user", "terranova"),
                Map.entry("header", true),
                Map.entry("time-standard", "UTC")
        );
    }

    private Map<String, Double> getDadosBrutos(NasaPowerDataResponse apiResponse) {
        return apiResponse.properties().parameter().entrySet().stream()
                .filter(entry -> PARAMETRO_PRECIPITACAO.equalsIgnoreCase(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Resposta da NASA POWER sem parametro prectotcorr."));
    }
}
