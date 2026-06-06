package fiap.com.br.terranova.integration;

import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.integration.nasa.NasaPowerIntegrationService;
import fiap.com.br.terranova.integration.satveg.SatVegIntegrationService;
import fiap.com.br.terranova.reqapi.ReqApi;
import fiap.com.br.terranova.reqapi.dto.ReqApiRequest;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import fiap.com.br.terranova.talhao.Talhao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DadoTemporalIntegrationService {

    private static final String API_NASA_POWER = "NASAPOWER";
    private static final String API_SATVEG = "SATVEG";
    private static final String PARAMETRO_PRECIPITACAO = "prectotcorr";
    private static final String PARAMETRO_NDVI = "ndvi";

    private final NasaPowerIntegrationService nasaPowerIntegrationService;
    private final SatVegIntegrationService satVegIntegrationService;

    public List<DadoTemporal> buscarDados(TipoApi tipoApi, ReqApiRequest request, Talhao talhao, ReqApi reqApi) {
        String tipo = tipoApi.getTipoApi();
        String parametro = request.tipoParam();

        validarTipoParamCompativel(tipo, parametro);

        if (API_NASA_POWER.equalsIgnoreCase(tipo)) {
            return nasaPowerIntegrationService.buscarDados(talhao, reqApi);
        }
        if (API_SATVEG.equalsIgnoreCase(tipo)) {
            return satVegIntegrationService.buscarDados(talhao, reqApi);
        }

        throw new IllegalArgumentException("Tipo de API nao suportado: " + tipo);
    }

    private void validarTipoParamCompativel(String tipoApiNome, String tipoParam) {
        if (API_NASA_POWER.equalsIgnoreCase(tipoApiNome) && !PARAMETRO_PRECIPITACAO.equalsIgnoreCase(tipoParam)) {
            throw new IllegalArgumentException("NASAPOWER suporta apenas o parametro prectotcorr.");
        }
        if (API_SATVEG.equalsIgnoreCase(tipoApiNome) && !PARAMETRO_NDVI.equalsIgnoreCase(tipoParam)) {
            throw new IllegalArgumentException("SATVEG suporta apenas o parametro ndvi.");
        }
    }
}
