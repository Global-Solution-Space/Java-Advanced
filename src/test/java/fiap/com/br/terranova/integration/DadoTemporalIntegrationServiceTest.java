package fiap.com.br.terranova.integration;

import fiap.com.br.terranova.integration.nasa.NasaPowerIntegrationService;
import fiap.com.br.terranova.integration.satveg.SatVegIntegrationService;
import fiap.com.br.terranova.reqapi.ReqApi;
import fiap.com.br.terranova.reqapi.dto.ReqApiRequest;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import fiap.com.br.terranova.talhao.Talhao;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DadoTemporalIntegrationServiceTest {

    private final NasaPowerIntegrationService nasaPowerIntegrationService = mock(NasaPowerIntegrationService.class);
    private final SatVegIntegrationService satVegIntegrationService = mock(SatVegIntegrationService.class);
    private final DadoTemporalIntegrationService service = new DadoTemporalIntegrationService(nasaPowerIntegrationService, satVegIntegrationService);

    @Test
    void shouldRouteNasaPowerRequest() {
        TipoApi tipoApi = TipoApi.builder().tipoApi("NASAPOWER").build();
        ReqApiRequest request = new ReqApiRequest("prectotcorr", "nasapower", 1L);
        Talhao talhao = new Talhao();
        ReqApi reqApi = new ReqApi();

        when(nasaPowerIntegrationService.buscarDados(talhao, reqApi)).thenReturn(List.of());

        service.buscarDados(tipoApi, request, talhao, reqApi);

        verify(nasaPowerIntegrationService, times(1)).buscarDados(talhao, reqApi);
        verify(satVegIntegrationService, never()).buscarDados(any(), any());
    }

    @Test
    void shouldRouteSatVegRequest() {
        TipoApi tipoApi = TipoApi.builder().tipoApi("SATVEG").build();
        ReqApiRequest request = new ReqApiRequest("ndvi", "satveg", 1L);
        Talhao talhao = new Talhao();
        ReqApi reqApi = new ReqApi();

        when(satVegIntegrationService.buscarDados(talhao, reqApi)).thenReturn(List.of());

        service.buscarDados(tipoApi, request, talhao, reqApi);

        verify(satVegIntegrationService, times(1)).buscarDados(talhao, reqApi);
        verify(nasaPowerIntegrationService, never()).buscarDados(any(), any());
    }

    @Test
    void shouldRejectIncompatibleParameter() {
        TipoApi tipoApi = TipoApi.builder().tipoApi("SATVEG").build();
        ReqApiRequest request = new ReqApiRequest("prectotcorr", "satveg", 1L);

        assertThrows(IllegalArgumentException.class, () -> service.buscarDados(tipoApi, request, new Talhao(), new ReqApi()));

        verifyNoInteractions(nasaPowerIntegrationService, satVegIntegrationService);
    }
}
