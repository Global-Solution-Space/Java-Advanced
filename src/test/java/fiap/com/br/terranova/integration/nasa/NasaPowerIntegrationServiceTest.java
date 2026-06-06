package fiap.com.br.terranova.integration.nasa;

import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.reqapi.ReqApi;
import fiap.com.br.terranova.talhao.Talhao;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NasaPowerIntegrationServiceTest {

    private final NasaPowerClient nasaPowerClient = mock(NasaPowerClient.class);
    private final NasaPowerIntegrationService service = new NasaPowerIntegrationService(nasaPowerClient);

    @Test
    void shouldMapNasaPowerResponseAndIgnoreInvalidValues() {
        Talhao talhao = createTalhao();
        ReqApi reqApi = new ReqApi();
        Map<String, Double> serie = new LinkedHashMap<>();
        serie.put("20200101", 5.5);
        serie.put("20200102", -999.0);
        serie.put("20200103", null);
        serie.put("20200104", 0.0);

        NasaPowerDataResponse response = new NasaPowerDataResponse(
                null,
                new NasaPowerDataResponse.Properties(Map.of("PRECTOTCORR", serie))
        );
        when(nasaPowerClient.getDailyData(anyMap())).thenReturn(response);

        List<DadoTemporal> dados = service.buscarDados(talhao, reqApi);

        assertEquals(2, dados.size());
        assertEquals(LocalDate.of(2020, 1, 1), dados.get(0).getDataLeitura());
        assertEquals(5.5, dados.get(0).getValor());
        assertEquals(LocalDate.of(2020, 1, 4), dados.get(1).getDataLeitura());
        assertEquals(0.0, dados.get(1).getValor());
        assertEquals(talhao, dados.get(0).getTalhao());
        assertEquals(reqApi, dados.get(0).getReqApi());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> queryCaptor = ArgumentCaptor.forClass(Map.class);
        verify(nasaPowerClient).getDailyData(queryCaptor.capture());
        Map<String, Object> query = queryCaptor.getValue();
        assertEquals("20200101", query.get("start"));
        assertEquals("prectotcorr", query.get("parameters"));
        assertEquals(talhao.getLocalizacao().getLocLatitude(), query.get("latitude"));
        assertEquals(talhao.getLocalizacao().getLocLongitude(), query.get("longitude"));
    }

    @Test
    void shouldRejectNasaResponseWithoutPrecipitationParameter() {
        when(nasaPowerClient.getDailyData(anyMap())).thenReturn(new NasaPowerDataResponse(
                null,
                new NasaPowerDataResponse.Properties(Map.of("OTHER", Map.of()))
        ));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.buscarDados(createTalhao(), new ReqApi()));

        assertEquals("Resposta da NASA POWER sem parametro prectotcorr.", exception.getMessage());
    }

    private Talhao createTalhao() {
        Localizacao localizacao = Localizacao.builder()
                .locLatitude(new BigDecimal("-23.5505"))
                .locLongitude(new BigDecimal("-46.6333"))
                .build();
        return Talhao.builder()
                .idTalhao(1L)
                .localizacao(localizacao)
                .build();
    }
}
