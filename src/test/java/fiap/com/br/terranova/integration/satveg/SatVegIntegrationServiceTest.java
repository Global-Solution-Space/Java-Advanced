package fiap.com.br.terranova.integration.satveg;

import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.reqapi.ReqApi;
import fiap.com.br.terranova.talhao.Talhao;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SatVegIntegrationServiceTest {

    private final SatVegClient satVegClient = mock(SatVegClient.class);
    private final SatVegIntegrationService service = new SatVegIntegrationService(satVegClient);

    @Test
    void shouldMapSatVegResponseAndIgnoreDatesBefore2020() {
        ReflectionTestUtils.setField(service, "satVegToken", "token-test");
        Talhao talhao = createTalhao();
        ReqApi reqApi = new ReqApi();
        SatVegDataResponse response = new SatVegDataResponse(
                List.of(0.1, 0.5, 0.7),
                List.of("2019-12-31", "2020-01-01", "2020-01-15")
        );
        when(satVegClient.getSeries(eq("token-test"), any(SatVegDataRequest.class))).thenReturn(response);

        List<DadoTemporal> dados = service.buscarDados(talhao, reqApi);

        assertEquals(2, dados.size());
        assertEquals(LocalDate.of(2020, 1, 1), dados.get(0).getDataLeitura());
        assertEquals(0.5, dados.get(0).getValor());
        assertEquals(LocalDate.of(2020, 1, 15), dados.get(1).getDataLeitura());
        assertEquals(0.7, dados.get(1).getValor());
        assertEquals(talhao, dados.get(0).getTalhao());
        assertEquals(reqApi, dados.get(0).getReqApi());

        ArgumentCaptor<SatVegDataRequest> requestCaptor = ArgumentCaptor.forClass(SatVegDataRequest.class);
        verify(satVegClient).getSeries(eq("token-test"), requestCaptor.capture());
        SatVegDataRequest request = requestCaptor.getValue();
        assertEquals("ndvi", request.getTipoPerfil());
        assertEquals("comb", request.getSatelite());
        assertEquals(3, request.getPreFiltro());
        assertEquals("sav", request.getFiltro());
        assertEquals(4, request.getParametroFiltro());
        assertEquals(BigDecimal.valueOf(talhao.getLocalizacao().getCoordenadas().getY()), request.getLatitude());
        assertEquals(BigDecimal.valueOf(talhao.getLocalizacao().getCoordenadas().getX()), request.getLongitude());
    }

    @Test
    void shouldRejectRequestWhenSatVegTokenIsMissing() {
        ReflectionTestUtils.setField(service, "satVegToken", "");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.buscarDados(createTalhao(), new ReqApi()));

        assertEquals("Token da API SATVeg nao configurado. Defina satveg.api.token ou SATVEG_API_TOKEN.", exception.getMessage());
        verify(satVegClient, never()).getSeries(any(), any());
    }

    private Talhao createTalhao() {
        Localizacao localizacao = Localizacao.builder()
                .coordenadas(new org.locationtech.jts.geom.GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326).createPoint(new org.locationtech.jts.geom.Coordinate(new BigDecimal("-46.6333").doubleValue(), new BigDecimal("-23.5505").doubleValue())))
                .build();
        return Talhao.builder()
                .idTalhao(1L)
                .localizacao(localizacao)
                .build();
    }
}
