package fiap.com.br.terranova.alerta;

import fiap.com.br.terranova.alerta.dto.AlertaRequest;
import fiap.com.br.terranova.alerta.dto.AlertaResponse;
import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.dadotemporal.DadoTemporalRepository;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private TalhaoRepository talhaoRepository;

    @Mock
    private DadoTemporalRepository dadoTemporalRepository;

    @InjectMocks
    private AlertaService alertaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFindAll() {
        Alerta alerta = new Alerta(1L, "Título", "Desc", "ALTO", "N", new Timestamp(System.currentTimeMillis()), new Talhao());
        Page<Alerta> page = new PageImpl<>(List.of(alerta));
        when(alertaRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<AlertaResponse> result = alertaService.findAll(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Título", result.getContent().get(0).titulo());
    }

    @Test
    void shouldFindByIdSuccessfully() {
        Alerta alerta = new Alerta(1L, "Título", "Desc", "ALTO", "N", new Timestamp(System.currentTimeMillis()), new Talhao());
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(alerta));

        AlertaResponse response = alertaService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void shouldThrowExceptionWhenAlertaNotFound() {
        when(alertaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertaService.findById(99L));
    }

    @Test
    void shouldFindByProdutor() {
        Talhao talhao = new Talhao();
        talhao.setIdTalhao(1L);
        Alerta alerta = new Alerta(1L, "Titulo", "Desc", "ALTO", "N", new Timestamp(System.currentTimeMillis()), talhao);
        when(alertaRepository.findByTalhaoPropriedadeProdutorIdProdutor(10L)).thenReturn(List.of(alerta));

        List<AlertaResponse> responses = alertaService.findByProdutor(10L);

        assertEquals(1, responses.size());
        assertEquals("Titulo", responses.get(0).titulo());
        verify(alertaRepository, times(1)).findByTalhaoPropriedadeProdutorIdProdutor(10L);
    }

    @Test
    void shouldCreateAlerta() {
        AlertaRequest request = new AlertaRequest("Título", "Desc", "ALTO", "N", 1L);
        Talhao talhao = new Talhao();
        talhao.setIdTalhao(1L);

        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(talhao));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(i -> {
            Alerta a = i.getArgument(0);
            a.setIdAlerta(1L);
            return a;
        });

        AlertaResponse response = alertaService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Título", response.titulo());
    }

    @Test
    void shouldThrowExceptionWhenCreatingAlertaWithInvalidTalhao() {
        AlertaRequest request = new AlertaRequest("Titulo", "Desc", "ALTO", "N", 99L);
        when(talhaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertaService.create(request));
        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    void shouldUpdateAlerta() {
        Alerta existing = new Alerta(1L, "Título", "Desc", "ALTO", "N", new Timestamp(System.currentTimeMillis()), new Talhao());
        Timestamp originalDataAlerta = existing.getDataAlerta();
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(existing));

        Talhao talhao = new Talhao();
        talhao.setIdTalhao(1L);
        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(talhao));

        AlertaRequest request = new AlertaRequest("Título Editado", "Desc", "BAIXO", "N", 1L);
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(i -> i.getArgument(0));

        AlertaResponse response = alertaService.update(1L, request);

        assertEquals("Título Editado", response.titulo());
        assertEquals("BAIXO", response.nivelAlerta());
        assertEquals(originalDataAlerta, response.dataAlerta());
    }

    @Test
    void shouldResolverAlerta() {
        Alerta existing = new Alerta(1L, "Título", "Desc", "ALTO", "N", new Timestamp(System.currentTimeMillis()), new Talhao());
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(i -> i.getArgument(0));

        AlertaResponse response = alertaService.resolver(1L);

        assertEquals("S", response.resolvido());
    }

    @Test
    void shouldReabrirAlerta() {
        Alerta existing = new Alerta(1L, "Título", "Desc", "ALTO", "S", new Timestamp(System.currentTimeMillis()), new Talhao());
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(i -> i.getArgument(0));

        AlertaResponse response = alertaService.reabrir(1L);

        assertEquals("N", response.resolvido());
    }

    @Test
    void shouldDeleteAlerta() {
        Alerta existing = new Alerta(1L, "Título", "Desc", "ALTO", "N", new Timestamp(System.currentTimeMillis()), new Talhao());
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(existing));

        alertaService.delete(1L);

        verify(alertaRepository, times(1)).delete(existing);
    }

    // --- ANÁLISE AUTOMÁTICA TESTS ---

    @Test
    void shouldNotGenerateAlertaWhenNoData() {
        Talhao talhao = new Talhao();
        talhao.setIdTalhao(1L);

        when(dadoTemporalRepository.findByTalhaoIdTalhaoAndReqApiTipoApiTipoApiIgnoreCaseAndDataLeituraAfter(
                eq(1L), eq("NASAPOWER"), any(LocalDate.class))).thenReturn(List.of());

        alertaService.analisarEGerarAlertas(talhao, "NASAPOWER");

        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    void shouldGenerateNasaAlagamentoAlerta() {
        Talhao talhao = new Talhao();
        talhao.setIdTalhao(1L);

        DadoTemporal d1 = DadoTemporal.builder().dataLeitura(LocalDate.now()).valor(30.0).build();
        DadoTemporal d2 = DadoTemporal.builder().dataLeitura(LocalDate.now().minusDays(1)).valor(30.0).build();
        DadoTemporal d3 = DadoTemporal.builder().dataLeitura(LocalDate.now().minusDays(2)).valor(30.0).build();
        // Soma 3 dias = 90.0 > 80.0 -> ALAGAMENTO

        when(dadoTemporalRepository.findByTalhaoIdTalhaoAndReqApiTipoApiTipoApiIgnoreCaseAndDataLeituraAfter(
                eq(1L), eq("nasapower"), any(LocalDate.class))).thenReturn(List.of(d1, d2, d3));
        
        when(alertaRepository.existsByTalhaoAndTituloAndResolvido(any(), anyString(), eq("N"))).thenReturn(false);

        alertaService.analisarEGerarAlertas(talhao, "nasapower");

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository, times(1)).save(captor.capture());
        assertEquals("Risco de Alagamento (NASA)", captor.getValue().getTitulo());
        assertEquals("ALTO", captor.getValue().getNivelAlerta());
    }

    @Test
    void shouldGenerateNasaSecaSeveraAlerta() {
        Talhao talhao = new Talhao();
        talhao.setIdTalhao(1L);

        DadoTemporal d1 = DadoTemporal.builder().dataLeitura(LocalDate.now()).valor(2.0).build();
        DadoTemporal d2 = DadoTemporal.builder().dataLeitura(LocalDate.now().minusDays(5)).valor(3.0).build();
        // Soma 15 dias = 5.0 < 10.0 -> SECA SEVERA

        when(dadoTemporalRepository.findByTalhaoIdTalhaoAndReqApiTipoApiTipoApiIgnoreCaseAndDataLeituraAfter(
                eq(1L), eq("nasapower"), any(LocalDate.class))).thenReturn(List.of(d1, d2));
        
        when(alertaRepository.existsByTalhaoAndTituloAndResolvido(any(), anyString(), eq("N"))).thenReturn(false);

        alertaService.analisarEGerarAlertas(talhao, "nasapower");

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository, times(1)).save(captor.capture());
        assertEquals("Seca Severa (NASA)", captor.getValue().getTitulo());
        assertEquals("CRITICO", captor.getValue().getNivelAlerta());
    }

    @Test
    void shouldNotGenerateDuplicateAutomaticAlertaWhenActiveExists() {
        Talhao talhao = new Talhao();
        talhao.setIdTalhao(1L);
        DadoTemporal d1 = DadoTemporal.builder().dataLeitura(LocalDate.now()).valor(2.0).build();
        DadoTemporal d2 = DadoTemporal.builder().dataLeitura(LocalDate.now().minusDays(1)).valor(3.0).build();

        when(dadoTemporalRepository.findByTalhaoIdTalhaoAndReqApiTipoApiTipoApiIgnoreCaseAndDataLeituraAfter(
                eq(1L), eq("nasapower"), any(LocalDate.class))).thenReturn(List.of(d1, d2));
        when(alertaRepository.existsByTalhaoAndTituloAndResolvido(talhao, "Seca Severa (NASA)", "N")).thenReturn(true);

        alertaService.analisarEGerarAlertas(talhao, "nasapower");

        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    void shouldGenerateSatvegAnomaliaAlerta() {
        Talhao talhao = new Talhao();
        talhao.setIdTalhao(1L);

        // NDVI atual = 0.15 (< 0.2)
        DadoTemporal d1 = DadoTemporal.builder().dataLeitura(LocalDate.now()).valor(0.15).build();

        when(dadoTemporalRepository.findByTalhaoIdTalhaoAndReqApiTipoApiTipoApiIgnoreCaseAndDataLeituraAfter(
                eq(1L), eq("satveg"), any(LocalDate.class))).thenReturn(List.of(d1));
        
        when(alertaRepository.existsByTalhaoAndTituloAndResolvido(any(), anyString(), eq("N"))).thenReturn(false);

        alertaService.analisarEGerarAlertas(talhao, "satveg");

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository, times(1)).save(captor.capture());
        assertEquals("Anomalia Vegetativa Severa (SATVEG)", captor.getValue().getTitulo());
        assertEquals("CRITICO", captor.getValue().getNivelAlerta());
    }
}
