package fiap.com.br.terranova.propriedade;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.localizacao.LocalizacaoRepository;
import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.produtor.ProdutorRepository;
import fiap.com.br.terranova.propriedade.dto.PropriedadeRequest;
import fiap.com.br.terranova.propriedade.dto.PropriedadeResponse;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import fiap.com.br.terranova.talhao.TalhaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PropriedadeServiceTest {

    @Mock
    private PropriedadeRepository propriedadeRepository;

    @Mock
    private ProdutorRepository produtorRepository;

    @Mock
    private LocalizacaoRepository localizacaoRepository;

    @Mock
    private TalhaoRepository talhaoRepository;

    @Mock
    private TalhaoService talhaoService;

    @InjectMocks
    private PropriedadeService service;

    private Produtor mockProdutor;
    private Localizacao mockLocalizacao;
    private Propriedade mockPropriedade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockProdutor = Produtor.builder().idProdutor(1L).nome("Enzo").email("enzo@fiap.com").build();
        mockLocalizacao = Localizacao.builder()
                .idLocalizacao(2L)
                .locLatitude(BigDecimal.valueOf(-23.5505))
                .locLongitude(BigDecimal.valueOf(-46.6333))
                .build();
        mockPropriedade = Propriedade.builder()
                .idPropriedade(10L)
                .nome("Fazenda Sol")
                .tamanhoTotal(500.0)
                .produtor(mockProdutor)
                .localizacao(mockLocalizacao)
                .build();
    }

    @Test
    void shouldFindAllPropriedades() {
        Page<Propriedade> page = new PageImpl<>(List.of(mockPropriedade));
        when(propriedadeRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<PropriedadeResponse> result = service.findAll(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("Fazenda Sol", result.getContent().get(0).nome());
        verify(propriedadeRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldFindPropriedadesByProdutor() {
        when(propriedadeRepository.findByProdutorIdProdutor(1L)).thenReturn(List.of(mockPropriedade));

        List<PropriedadeResponse> result = service.findByProdutor(1L);

        assertEquals(1, result.size());
        assertEquals("Fazenda Sol", result.get(0).nome());
        verify(propriedadeRepository, times(1)).findByProdutorIdProdutor(1L);
    }

    @Test
    void shouldFindPropriedadeById() {
        when(propriedadeRepository.findById(10L)).thenReturn(Optional.of(mockPropriedade));

        PropriedadeResponse result = service.findById(10L);

        assertNotNull(result);
        assertEquals("Fazenda Sol", result.nome());
        verify(propriedadeRepository, times(1)).findById(10L);
    }

    @Test
    void shouldThrowExceptionWhenPropriedadeNotFound() {
        when(propriedadeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void shouldCreatePropriedadeSuccessfully() {
        PropriedadeRequest request = new PropriedadeRequest("Fazenda Sol", 500.0, 1L, 2L);
        when(produtorRepository.findById(1L)).thenReturn(Optional.of(mockProdutor));
        when(localizacaoRepository.findById(2L)).thenReturn(Optional.of(mockLocalizacao));
        when(propriedadeRepository.save(any(Propriedade.class))).thenReturn(mockPropriedade);

        PropriedadeResponse result = service.create(request);

        assertNotNull(result);
        assertEquals("Fazenda Sol", result.nome());
        verify(propriedadeRepository, times(1)).save(any(Propriedade.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingPropriedadeWithInvalidProdutor() {
        PropriedadeRequest request = new PropriedadeRequest("Fazenda Sol", 500.0, 99L, 2L);
        when(produtorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void shouldThrowExceptionWhenCreatingPropriedadeWithInvalidLocalizacao() {
        PropriedadeRequest request = new PropriedadeRequest("Fazenda Sol", 500.0, 1L, 99L);
        when(produtorRepository.findById(1L)).thenReturn(Optional.of(mockProdutor));
        when(localizacaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenLocalizacaoAlreadyUsedByAnotherPropriedade() {
        PropriedadeRequest request = new PropriedadeRequest("Fazenda Sol", 500.0, 1L, 2L);

        when(produtorRepository.findById(1L)).thenReturn(Optional.of(mockProdutor));
        when(localizacaoRepository.findById(2L)).thenReturn(Optional.of(mockLocalizacao));
        when(propriedadeRepository.existsByLocalizacaoIdLocalizacao(2L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
        verify(propriedadeRepository, never()).save(any(Propriedade.class));
    }

    @Test
    void shouldUpdatePropriedadeSuccessfully() {
        PropriedadeRequest request = new PropriedadeRequest("Fazenda Alterada", 600.0, 1L, 2L);
        Propriedade updatedPropriedade = Propriedade.builder()
                .idPropriedade(10L)
                .nome("Fazenda Alterada")
                .tamanhoTotal(600.0)
                .produtor(mockProdutor)
                .localizacao(mockLocalizacao)
                .build();

        when(propriedadeRepository.findById(10L)).thenReturn(Optional.of(mockPropriedade));
        when(produtorRepository.findById(1L)).thenReturn(Optional.of(mockProdutor));
        when(localizacaoRepository.findById(2L)).thenReturn(Optional.of(mockLocalizacao));
        when(propriedadeRepository.save(any(Propriedade.class))).thenReturn(updatedPropriedade);

        PropriedadeResponse result = service.update(10L, request);

        assertNotNull(result);
        assertEquals("Fazenda Alterada", result.nome());
        verify(propriedadeRepository, times(1)).save(any(Propriedade.class));
    }

    @Test
    void shouldDeletePropriedadeAndCascadeDeleteTalhoes() {
        when(propriedadeRepository.findById(10L)).thenReturn(Optional.of(mockPropriedade));
        
        // Mock de talhões associados a esta propriedade
        Talhao talhao1 = Talhao.builder().idTalhao(101L).build();
        Talhao talhao2 = Talhao.builder().idTalhao(102L).build();
        when(talhaoRepository.findByPropriedadeIdPropriedade(10L)).thenReturn(List.of(talhao1, talhao2));

        service.delete(10L);

        // Verifica que o service deletou cada talhão encontrado
        verify(talhaoService, times(1)).delete(101L);
        verify(talhaoService, times(1)).delete(102L);
        
        // Verifica que a própria propriedade foi deletada
        verify(propriedadeRepository, times(1)).delete(mockPropriedade);
    }
}
