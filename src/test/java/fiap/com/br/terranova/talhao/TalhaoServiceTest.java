package fiap.com.br.terranova.talhao;

import fiap.com.br.terranova.alerta.Alerta;
import fiap.com.br.terranova.alerta.AlertaRepository;
import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.dadotemporal.DadoTemporalRepository;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.localizacao.LocalizacaoRepository;
import fiap.com.br.terranova.propriedade.Propriedade;
import fiap.com.br.terranova.propriedade.PropriedadeRepository;
import fiap.com.br.terranova.talhao.dto.TalhaoRequest;
import fiap.com.br.terranova.talhao.dto.TalhaoResponse;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacao;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TalhaoServiceTest {

    @Mock
    private TalhaoRepository talhaoRepository;

    @Mock
    private TipoPlantacaoRepository tipoPlantacaoRepository;

    @Mock
    private PropriedadeRepository propriedadeRepository;

    @Mock
    private LocalizacaoRepository localizacaoRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private DadoTemporalRepository dadoTemporalRepository;

    @InjectMocks
    private TalhaoService service;

    private TipoPlantacao mockTipoPlantacao;
    private Propriedade mockPropriedade;
    private Localizacao mockLocalizacao;
    private Talhao mockTalhao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockTipoPlantacao = TipoPlantacao.builder().idTipoPlant(1L).tipoPlant("Milho").build();
        mockPropriedade = Propriedade.builder().idPropriedade(2L).nome("Fazenda Milho").tamanhoTotal(100.0).build();
        mockLocalizacao = Localizacao.builder().idLocalizacao(3L).build();
        mockTalhao = Talhao.builder()
                .idTalhao(10L)
                .nomeTalhao("Talhao A")
                .volumArea(25.0)
                .tipoPlantacao(mockTipoPlantacao)
                .propriedade(mockPropriedade)
                .localizacao(mockLocalizacao)
                .build();
    }

    @Test
    void shouldFindAllTalhoes() {
        Page<Talhao> page = new PageImpl<>(List.of(mockTalhao));
        when(talhaoRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<TalhaoResponse> result = service.findAll(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("Talhao A", result.getContent().get(0).nomeTalhao());
    }

    @Test
    void shouldFindTalhoesByProdutorId() {
        when(talhaoRepository.findByPropriedadeProdutorIdProdutor(1L)).thenReturn(List.of(mockTalhao));

        List<TalhaoResponse> result = service.findByProdutor(1L);

        assertEquals(1, result.size());
        assertEquals("Talhao A", result.get(0).nomeTalhao());
    }

    @Test
    void shouldFindTalhaoById() {
        when(talhaoRepository.findById(10L)).thenReturn(Optional.of(mockTalhao));

        TalhaoResponse response = service.findById(10L);

        assertNotNull(response);
        assertEquals(10L, response.id());
    }

    @Test
    void shouldThrowResourceNotFoundWhenTalhaoDoesNotExist() {
        when(talhaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void shouldCreateTalhaoSuccessfully() {
        TalhaoRequest request = new TalhaoRequest("Talhao A", 25.0, 1L, 2L, 3L);

        when(tipoPlantacaoRepository.findById(1L)).thenReturn(Optional.of(mockTipoPlantacao));
        when(propriedadeRepository.findById(2L)).thenReturn(Optional.of(mockPropriedade));
        when(localizacaoRepository.findById(3L)).thenReturn(Optional.of(mockLocalizacao));
        when(talhaoRepository.save(any(Talhao.class))).thenReturn(mockTalhao);

        TalhaoResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("Talhao A", response.nomeTalhao());
        verify(talhaoRepository, times(1)).save(any(Talhao.class));
    }

    @Test
    void shouldThrowResourceNotFoundWhenCreatingWithInvalidTipoPlantacao() {
        TalhaoRequest request = new TalhaoRequest("Talhao A", 25.0, 99L, 2L, 3L);

        when(tipoPlantacaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void shouldThrowResourceNotFoundWhenCreatingWithInvalidPropriedade() {
        TalhaoRequest request = new TalhaoRequest("Talhao A", 25.0, 1L, 99L, 3L);

        when(tipoPlantacaoRepository.findById(1L)).thenReturn(Optional.of(mockTipoPlantacao));
        when(propriedadeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void shouldThrowResourceNotFoundWhenCreatingWithInvalidLocalizacao() {
        TalhaoRequest request = new TalhaoRequest("Talhao A", 25.0, 1L, 2L, 99L);

        when(tipoPlantacaoRepository.findById(1L)).thenReturn(Optional.of(mockTipoPlantacao));
        when(propriedadeRepository.findById(2L)).thenReturn(Optional.of(mockPropriedade));
        when(localizacaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void shouldUpdateTalhaoSuccessfully() {
        TalhaoRequest request = new TalhaoRequest("Talhao Atualizado", 30.0, 1L, 2L, 3L);
        Talhao updatedTalhao = Talhao.builder()
                .idTalhao(10L)
                .nomeTalhao("Talhao Atualizado")
                .volumArea(30.0)
                .tipoPlantacao(mockTipoPlantacao)
                .propriedade(mockPropriedade)
                .localizacao(mockLocalizacao)
                .build();

        when(talhaoRepository.findById(10L)).thenReturn(Optional.of(mockTalhao));
        when(tipoPlantacaoRepository.findById(1L)).thenReturn(Optional.of(mockTipoPlantacao));
        when(propriedadeRepository.findById(2L)).thenReturn(Optional.of(mockPropriedade));
        when(localizacaoRepository.findById(3L)).thenReturn(Optional.of(mockLocalizacao));
        when(talhaoRepository.save(any(Talhao.class))).thenReturn(updatedTalhao);

        TalhaoResponse response = service.update(10L, request);

        assertNotNull(response);
        assertEquals("Talhao Atualizado", response.nomeTalhao());
        assertEquals(30.0, response.volumArea());
        verify(talhaoRepository, times(1)).save(any(Talhao.class));
    }

    @Test
    void shouldDeleteTalhaoWithCascades() {
        when(talhaoRepository.findById(10L)).thenReturn(Optional.of(mockTalhao));
        
        List<Alerta> mockAlertas = List.of(new Alerta());
        List<DadoTemporal> mockDados = List.of(new DadoTemporal());
        
        when(alertaRepository.findByTalhaoIdTalhao(10L)).thenReturn(mockAlertas);
        when(dadoTemporalRepository.findAllByTalhaoIdTalhao(10L)).thenReturn(mockDados);

        service.delete(10L);

        verify(alertaRepository, times(1)).deleteAll(mockAlertas);
        verify(dadoTemporalRepository, times(1)).deleteAll(mockDados);
        verify(talhaoRepository, times(1)).delete(mockTalhao);
    }
}
