package fiap.com.br.terranova.localizacao;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.localizacao.dto.LocalizacaoRequest;
import fiap.com.br.terranova.localizacao.dto.LocalizacaoResponse;
import fiap.com.br.terranova.propriedade.PropriedadeRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LocalizacaoServiceTest {

    @Mock
    private LocalizacaoRepository repository;

    @Mock
    private PropriedadeRepository propriedadeRepository;

    @Mock
    private TalhaoRepository talhaoRepository;

    @InjectMocks
    private LocalizacaoService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFindAllLocalizacoes() {
        Localizacao localizacao = Localizacao.builder()
                .idLocalizacao(1L)
                .locLatitude(new BigDecimal("-23.5505"))
                .locLongitude(new BigDecimal("-46.6333"))
                .build();
        Page<Localizacao> page = new PageImpl<>(List.of(localizacao));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<LocalizacaoResponse> result = service.findAll(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).id());
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldFindLocalizacaoById() {
        Localizacao localizacao = Localizacao.builder()
                .idLocalizacao(1L)
                .locLatitude(new BigDecimal("-23.5505"))
                .locLongitude(new BigDecimal("-46.6333"))
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(localizacao));

        LocalizacaoResponse response = service.findById(1L);

        assertEquals(1L, response.id());
        assertEquals(new BigDecimal("-23.5505"), response.locLatitude());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenLocalizacaoNotFoundById() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void shouldCreateNewLocalizacaoEvenWhenCoordinatesAreDuplicate() {
        // Arrange
        BigDecimal lat = new BigDecimal("-23.5505");
        BigDecimal lon = new BigDecimal("-46.6333");
        LocalizacaoRequest request = new LocalizacaoRequest(lat, lon);
        
        Localizacao savedEntity = Localizacao.builder()
                .idLocalizacao(99L)
                .locLatitude(lat)
                .locLongitude(lon)
                .build();

        // Configura o repositório para retornar a entidade existente
        when(repository.save(any(Localizacao.class))).thenReturn(savedEntity);

        // Act
        LocalizacaoResponse response = service.create(request);

        // Assert
        assertEquals(99L, response.id(), "Deveria criar uma nova localização mesmo com coordenadas repetidas.");
        assertEquals(lat, response.locLatitude());
        assertEquals(lon, response.locLongitude());
        
        // Verifica que o repository.save NUNCA foi chamado, prevenindo duplicatas!
        verify(repository, times(1)).save(any(Localizacao.class));
    }

    @Test
    void shouldCreateNewLocalizacaoWhenCoordinatesAreNew() {
        // Arrange
        BigDecimal lat = new BigDecimal("-23.5505");
        BigDecimal lon = new BigDecimal("-46.6333");
        LocalizacaoRequest request = new LocalizacaoRequest(lat, lon);
        
        Localizacao newEntity = Localizacao.builder()
                .idLocalizacao(100L)
                .locLatitude(lat)
                .locLongitude(lon)
                .build();

        // Configura o repositório para NÃO encontrar a entidade
        // Configura o save para retornar a entidade salva
        when(repository.save(any(Localizacao.class))).thenReturn(newEntity);

        // Act
        LocalizacaoResponse response = service.create(request);

        // Assert
        assertEquals(100L, response.id(), "Deveria ter retornado o ID da nova localização criada.");
        
        // Verifica que o repository.save FOI chamado
        verify(repository, times(1)).save(any(Localizacao.class));
    }

    @Test
    void shouldUpdateLocalizacao() {
        Localizacao existing = Localizacao.builder()
                .idLocalizacao(1L)
                .locLatitude(new BigDecimal("-23.5505"))
                .locLongitude(new BigDecimal("-46.6333"))
                .build();
        LocalizacaoRequest request = new LocalizacaoRequest(new BigDecimal("-15.7801"), new BigDecimal("-47.9292"));
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Localizacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalizacaoResponse response = service.update(1L, request);

        assertEquals(1L, response.id());
        assertEquals(new BigDecimal("-15.7801"), response.locLatitude());

        ArgumentCaptor<Localizacao> captor = ArgumentCaptor.forClass(Localizacao.class);
        verify(repository).save(captor.capture());
        assertEquals(1L, captor.getValue().getIdLocalizacao());
        assertEquals(new BigDecimal("-47.9292"), captor.getValue().getLocLongitude());
    }

    @Test
    void shouldDeleteLocalizacao() {
        Localizacao existing = Localizacao.builder()
                .idLocalizacao(1L)
                .locLatitude(new BigDecimal("-23.5505"))
                .locLongitude(new BigDecimal("-46.6333"))
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(propriedadeRepository.existsByLocalizacaoIdLocalizacao(1L)).thenReturn(false);
        when(talhaoRepository.existsByLocalizacaoIdLocalizacao(1L)).thenReturn(false);

        service.delete(1L);

        verify(repository, times(1)).delete(existing);
    }

    @Test
    void shouldRejectDeleteWhenLocalizacaoIsInUse() {
        Localizacao existing = Localizacao.builder()
                .idLocalizacao(1L)
                .locLatitude(new BigDecimal("-23.5505"))
                .locLongitude(new BigDecimal("-46.6333"))
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(propriedadeRepository.existsByLocalizacaoIdLocalizacao(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.delete(1L));

        verify(repository, never()).delete(any(Localizacao.class));
    }
}
