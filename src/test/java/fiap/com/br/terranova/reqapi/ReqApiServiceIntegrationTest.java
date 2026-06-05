package fiap.com.br.terranova.reqapi;

import fiap.com.br.terranova.alerta.AlertaService;
import fiap.com.br.terranova.dadotemporal.DadoTemporalRepository;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.integration.nasa.NasaPowerClient;
import fiap.com.br.terranova.integration.nasa.NasaPowerDataResponse;
import fiap.com.br.terranova.integration.satveg.SatVegClient;
import fiap.com.br.terranova.integration.satveg.SatVegDataRequest;
import fiap.com.br.terranova.integration.satveg.SatVegDataResponse;
import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.reqapi.dto.ReqApiRequest;
import fiap.com.br.terranova.reqapi.dto.ReqApiResponse;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApiRepository;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ReqApiServiceIntegrationTest {

    @Mock
    private ReqApiRepository reqApiRepository;
    @Mock
    private TipoApiRepository tipoApiRepository;
    @Mock
    private TalhaoRepository talhaoRepository;
    @Mock
    private DadoTemporalRepository dadoTemporalRepository;
    @Mock
    private AlertaService alertaService;
    @Mock
    private NasaPowerClient nasaPowerClient;
    @Mock
    private SatVegClient satVegClient;

    @InjectMocks
    private ReqApiService service;

    private Talhao mockTalhao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        Localizacao loc = Localizacao.builder()
                .locLatitude(new BigDecimal("-23.55"))
                .locLongitude(new BigDecimal("-46.63"))
                .build();
                
        mockTalhao = Talhao.builder().idTalhao(1L).localizacao(loc).build();
    }

    @Test
    void shouldCreateReqApiAndFetchNasaData() {
        // Arrange
        ReqApiRequest request = new ReqApiRequest("PRECTOTCORR", "NASAPOWER", 1L);
        TipoApi tipoNasa = TipoApi.builder().idTipo(1L).tipoApi("NASAPOWER").build();

        when(tipoApiRepository.findByTipoApi("NASAPOWER")).thenReturn(Optional.of(tipoNasa));
        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(mockTalhao));
        when(reqApiRepository.save(any(ReqApi.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mocking the NASA OpenFeign Client response
        NasaPowerDataResponse.Properties properties = new NasaPowerDataResponse.Properties(Map.of("PRECTOTCORR", Map.of("20240101", 12.5)));
        NasaPowerDataResponse nasaResponse = new NasaPowerDataResponse(null, properties);
        when(nasaPowerClient.getDailyData(anyMap())).thenReturn(nasaResponse);

        // Act
        ReqApiResponse response = service.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("NASAPOWER", response.tipoApiNome());
        verify(nasaPowerClient, times(1)).getDailyData(anyMap());
        verify(dadoTemporalRepository, times(1)).saveAll(anyList());
        verify(alertaService, times(1)).analisarEGerarAlertas(eq(mockTalhao), eq("NASAPOWER"));
        verify(satVegClient, never()).getSeries(anyString(), any(SatVegDataRequest.class));
    }

    @Test
    void shouldCreateReqApiAndFetchSatVegData() {
        // Arrange
        ReqApiRequest request = new ReqApiRequest("NDVI", "SATVEG", 1L);
        TipoApi tipoSatVeg = TipoApi.builder().idTipo(2L).tipoApi("SATVEG").build();

        when(tipoApiRepository.findByTipoApi("SATVEG")).thenReturn(Optional.of(tipoSatVeg));
        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(mockTalhao));
        when(reqApiRepository.save(any(ReqApi.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mocking the SATveg OpenFeign Client response
        SatVegDataResponse satVegResponse = new SatVegDataResponse(List.of(0.85), List.of("2020-01-01"));
        when(satVegClient.getSeries(any(), any(SatVegDataRequest.class))).thenReturn(satVegResponse);

        // Act
        ReqApiResponse response = service.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("SATVEG", response.tipoApiNome());
        verify(satVegClient, times(1)).getSeries(any(), any(SatVegDataRequest.class));
        verify(dadoTemporalRepository, times(1)).saveAll(anyList());
        verify(alertaService, times(1)).analisarEGerarAlertas(eq(mockTalhao), eq("SATVEG"));
        verify(nasaPowerClient, never()).getDailyData(anyMap());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenNasaReturnsBadRequest() {
        // Arrange
        ReqApiRequest request = new ReqApiRequest("PRECTOTCORR", "NASAPOWER", 1L);
        TipoApi tipoNasa = TipoApi.builder().idTipo(1L).tipoApi("NASAPOWER").build();

        when(tipoApiRepository.findByTipoApi("NASAPOWER")).thenReturn(Optional.of(tipoNasa));
        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(mockTalhao));
        when(reqApiRepository.save(any(ReqApi.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mocking a Feign 400 Bad Request exception
        feign.Request feignRequest = feign.Request.create(
                feign.Request.HttpMethod.GET,
                "https://power.larc.nasa.gov",
                Map.of(),
                feign.Request.Body.empty(),
                null
        );
        feign.FeignException.BadRequest badRequestEx = new feign.FeignException.BadRequest(
                "Bad Request",
                feignRequest,
                "{\"detail\":\"Latitude out of bounds\"}".getBytes(),
                Map.of()
        );

        when(nasaPowerClient.getDailyData(anyMap())).thenThrow(badRequestEx);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.create(request);
        });

        assertTrue(exception.getMessage().contains("Erro na integracao com NASA POWER: Latitude out of bounds"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSatVegReturnsBadRequest() {
        // Arrange
        ReqApiRequest request = new ReqApiRequest("NDVI", "SATVEG", 1L);
        TipoApi tipoSatVeg = TipoApi.builder().idTipo(2L).tipoApi("SATVEG").build();

        when(tipoApiRepository.findByTipoApi("SATVEG")).thenReturn(Optional.of(tipoSatVeg));
        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(mockTalhao));
        when(reqApiRepository.save(any(ReqApi.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mocking a Feign 400 Bad Request exception
        feign.Request feignRequest = feign.Request.create(
                feign.Request.HttpMethod.POST,
                "https://api.cnptia.embrapa.br",
                Map.of(),
                feign.Request.Body.empty(),
                null
        );
        feign.FeignException.BadRequest badRequestEx = new feign.FeignException.BadRequest(
                "Bad Request",
                feignRequest,
                "{\"detail\":\"Coordenadas invalidas\"}".getBytes(),
                Map.of()
        );

        when(satVegClient.getSeries(any(), any(SatVegDataRequest.class))).thenThrow(badRequestEx);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.create(request);
        });

        assertTrue(exception.getMessage().contains("Erro na integracao com SATveg: Coordenadas invalidas"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenNasaThrowsGenericException() {
        // Arrange
        ReqApiRequest request = new ReqApiRequest("PRECTOTCORR", "NASAPOWER", 1L);
        TipoApi tipoNasa = TipoApi.builder().idTipo(1L).tipoApi("NASAPOWER").build();

        when(tipoApiRepository.findByTipoApi("NASAPOWER")).thenReturn(Optional.of(tipoNasa));
        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(mockTalhao));
        when(reqApiRepository.save(any(ReqApi.class))).thenAnswer(i -> i.getArguments()[0]);

        when(nasaPowerClient.getDailyData(anyMap())).thenThrow(new RuntimeException("Timeout na conexao"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.create(request);
        });

        assertTrue(exception.getMessage().contains("Erro inesperado na integracao com a NASA POWER: Timeout na conexao"));
    }

    @Test
    void shouldFindAllReqApis() {
        TipoApi tipo = TipoApi.builder().idTipo(1L).tipoApi("NASAPOWER").build();
        ReqApi reqApi = ReqApi.builder().idApi(10L).tipoParam("PRECTOTCORR").tipoApi(tipo).build();
        
        org.springframework.data.domain.Page<ReqApi> page = new org.springframework.data.domain.PageImpl<>(List.of(reqApi));
        when(reqApiRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        org.springframework.data.domain.Page<ReqApiResponse> result = service.findAll(org.springframework.data.domain.Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("NASAPOWER", result.getContent().get(0).tipoApiNome());
    }

    @Test
    void shouldFindReqApiById() {
        TipoApi tipo = TipoApi.builder().idTipo(1L).tipoApi("NASAPOWER").build();
        ReqApi reqApi = ReqApi.builder().idApi(10L).tipoParam("PRECTOTCORR").tipoApi(tipo).build();
        when(reqApiRepository.findById(10L)).thenReturn(Optional.of(reqApi));

        ReqApiResponse response = service.findById(10L);

        assertNotNull(response);
        assertEquals(10L, response.id());
    }

    @Test
    void shouldThrowResourceNotFoundWhenReqApiNotFound() {
        when(reqApiRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void shouldFindReqApisByTalhaoId() {
        TipoApi tipo = TipoApi.builder().idTipo(1L).tipoApi("NASAPOWER").build();
        ReqApi reqApi = ReqApi.builder().idApi(10L).tipoParam("PRECTOTCORR").tipoApi(tipo).build();
        when(reqApiRepository.findByTalhao_IdTalhao(1L)).thenReturn(List.of(reqApi));

        List<ReqApiResponse> list = service.findByTalhaoId(1L);

        assertEquals(1, list.size());
        assertEquals(10L, list.get(0).id());
    }

    @Test
    void shouldDeleteReqApiSuccessfully() {
        TipoApi tipo = TipoApi.builder().idTipo(1L).tipoApi("NASAPOWER").build();
        ReqApi reqApi = ReqApi.builder().idApi(10L).tipoParam("PRECTOTCORR").tipoApi(tipo).build();
        when(reqApiRepository.findById(10L)).thenReturn(Optional.of(reqApi));

        service.delete(10L);

        verify(reqApiRepository, times(1)).delete(reqApi);
    }

    @Test
    void shouldThrowResourceNotFoundWhenDeletingNonExistentReqApi() {
        when(reqApiRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }

    @Test
    void shouldThrowResourceNotFoundWhenCreatingWithInvalidTalhao() {
        ReqApiRequest request = new ReqApiRequest("PRECTOTCORR", "NASAPOWER", 99L);
        TipoApi tipo = TipoApi.builder().idTipo(1L).tipoApi("NASAPOWER").build();

        when(tipoApiRepository.findByTipoApi("NASAPOWER")).thenReturn(Optional.of(tipo));
        when(talhaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void shouldThrowResourceNotFoundWhenCreatingWithInvalidTipoApi() {
        ReqApiRequest request = new ReqApiRequest("PRECTOTCORR", "INVALID_API", 1L);

        when(tipoApiRepository.findByTipoApi("INVALID_API")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }
}
