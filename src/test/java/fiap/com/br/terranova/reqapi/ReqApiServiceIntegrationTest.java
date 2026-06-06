package fiap.com.br.terranova.reqapi;

import fiap.com.br.terranova.alerta.AlertaService;
import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.dadotemporal.DadoTemporalRepository;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.integration.DadoTemporalIntegrationService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    private DadoTemporalIntegrationService dadoTemporalIntegrationService;

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
    void shouldCreateReqApiAndPersistIntegratedData() {
        ReqApiRequest request = new ReqApiRequest("NDVI", "satveg", 1L);
        TipoApi tipoSatVeg = TipoApi.builder().idTipo(2L).tipoApi("SATVEG").build();

        when(tipoApiRepository.findByTipoApi("satveg")).thenReturn(Optional.of(tipoSatVeg));
        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(mockTalhao));
        when(reqApiRepository.save(any(ReqApi.class))).thenAnswer(i -> i.getArgument(0));
        when(dadoTemporalIntegrationService.buscarDados(eq(tipoSatVeg), eq(request), eq(mockTalhao), any(ReqApi.class)))
                .thenReturn(List.of(DadoTemporal.builder().dataLeitura(LocalDate.now()).valor(0.85).build()));

        ReqApiResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("SATVEG", response.tipoApiNome());
        assertEquals("NDVI", response.tipoParam());
        verify(dadoTemporalIntegrationService, times(1)).buscarDados(eq(tipoSatVeg), eq(request), eq(mockTalhao), any(ReqApi.class));
        verify(dadoTemporalRepository, times(1)).saveAll(anyList());
        verify(alertaService, times(1)).analisarEGerarAlertas(eq(mockTalhao), eq("SATVEG"));
    }

    @Test
    void shouldPropagateIntegrationValidationErrors() {
        ReqApiRequest request = new ReqApiRequest("PRECTOTCORR", "SATVEG", 1L);
        TipoApi tipoSatVeg = TipoApi.builder().idTipo(2L).tipoApi("SATVEG").build();

        when(tipoApiRepository.findByTipoApi("SATVEG")).thenReturn(Optional.of(tipoSatVeg));
        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(mockTalhao));
        when(reqApiRepository.save(any(ReqApi.class))).thenAnswer(i -> i.getArgument(0));
        when(dadoTemporalIntegrationService.buscarDados(eq(tipoSatVeg), eq(request), eq(mockTalhao), any(ReqApi.class)))
                .thenThrow(new IllegalArgumentException("SATVEG suporta apenas o parametro NDVI."));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.create(request));

        assertTrue(exception.getMessage().contains("SATVEG suporta apenas o parametro NDVI"));
        verify(dadoTemporalRepository, never()).saveAll(anyList());
        verify(alertaService, never()).analisarEGerarAlertas(any(), any());
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
        when(reqApiRepository.findByTalhao_IdTalhao(eq(1L), any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(reqApi)));

        org.springframework.data.domain.Page<ReqApiResponse> list = service.findByTalhaoId(1L, org.springframework.data.domain.Pageable.unpaged());

        assertEquals(1, list.getContent().size());
        assertEquals(10L, list.getContent().get(0).id());
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
