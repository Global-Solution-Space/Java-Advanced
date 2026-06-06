package fiap.com.br.terranova.integration.satveg;

import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.reqapi.ReqApi;
import fiap.com.br.terranova.talhao.Talhao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static fiap.com.br.terranova.integration.FeignErrorUtil.extractDetail;

@Slf4j
@Service
@RequiredArgsConstructor
public class SatVegIntegrationService {

    private static final String DATA_INICIO = "2020-01-01";

    private final SatVegClient satVegClient;

    @Value("${satveg.api.token:}")
    private String satVegToken;

    public List<DadoTemporal> buscarDados(Talhao talhao, ReqApi reqApi) {
        try {
            if (satVegToken == null || satVegToken.isBlank()) {
                throw new IllegalArgumentException("Token da API SATVeg nao configurado. Defina satveg.api.token ou SATVEG_API_TOKEN.");
            }

            log.info("Buscando series temporais na Embrapa SATveg para o Talhao {}", talhao.getIdTalhao());
            SatVegDataResponse apiResponse = satVegClient.getSeries(satVegToken, buildRequest(talhao));

            List<DadoTemporal> dados = new ArrayList<>();
            for (int i = 0; i < apiResponse.listaSerie().size(); i++) {
                String dataString = apiResponse.listaDatas().get(i);
                if (dataString != null && dataString.compareTo(DATA_INICIO) >= 0) {
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
            throw new IllegalArgumentException("Erro na integracao com SATveg: " + extractDetail(e.contentUTF8()) + " (Verifique se a coordenada informada nao cai no oceano ou em paises vizinhos).");
        } catch (feign.FeignException e) {
            log.error("Erro na API da Embrapa SATveg", e);
            throw new IllegalArgumentException("Falha na integracao com SATveg. Status: " + e.status());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao integrar com a API da Embrapa SATveg", e);
            throw new IllegalArgumentException("Erro inesperado na integracao com a Embrapa SATveg: " + e.getMessage());
        }
    }

    private SatVegDataRequest buildRequest(Talhao talhao) {
        return SatVegDataRequest.builder()
                .tipoPerfil("ndvi")
                .satelite("comb")
                .preFiltro(3)
                .filtro("sav")
                .parametroFiltro(4)
                .latitude(talhao.getLocalizacao().getLocLatitude())
                .longitude(talhao.getLocalizacao().getLocLongitude())
                .build();
    }
}
