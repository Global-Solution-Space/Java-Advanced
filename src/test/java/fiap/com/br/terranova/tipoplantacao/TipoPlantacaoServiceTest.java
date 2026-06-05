package fiap.com.br.terranova.tipoplantacao;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.tipoplantacao.dto.TipoPlantacaoRequest;
import fiap.com.br.terranova.tipoplantacao.dto.TipoPlantacaoResponse;
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

class TipoPlantacaoServiceTest {

    @Mock
    private TipoPlantacaoRepository repository;

    @InjectMocks
    private TipoPlantacaoService service;

    private TipoPlantacao mockTipoPlantacao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockTipoPlantacao = TipoPlantacao.builder()
                .idTipoPlant(1L)
                .tipoPlant("Soja")
                .build();
    }

    @Test
    void shouldFindAllTiposPlantacao() {
        Page<TipoPlantacao> page = new PageImpl<>(List.of(mockTipoPlantacao));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<TipoPlantacaoResponse> result = service.findAll(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("Soja", result.getContent().get(0).tipoPlant());
    }

    @Test
    void shouldFindTipoPlantacaoById() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockTipoPlantacao));

        TipoPlantacaoResponse response = service.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Soja", response.tipoPlant());
    }

    @Test
    void shouldThrowResourceNotFoundWhenTipoPlantacaoDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void shouldCreateTipoPlantacaoSuccessfully() {
        TipoPlantacaoRequest request = new TipoPlantacaoRequest("Milho");
        TipoPlantacao savedEntity = TipoPlantacao.builder().idTipoPlant(2L).tipoPlant("Milho").build();

        when(repository.save(any(TipoPlantacao.class))).thenReturn(savedEntity);

        TipoPlantacaoResponse response = service.create(request);

        assertNotNull(response);
        assertEquals(2L, response.id());
        assertEquals("Milho", response.tipoPlant());
        verify(repository, times(1)).save(any(TipoPlantacao.class));
    }

    @Test
    void shouldUpdateTipoPlantacaoSuccessfully() {
        TipoPlantacaoRequest request = new TipoPlantacaoRequest("Trigo");
        TipoPlantacao updatedEntity = TipoPlantacao.builder().idTipoPlant(1L).tipoPlant("Trigo").build();

        when(repository.findById(1L)).thenReturn(Optional.of(mockTipoPlantacao));
        when(repository.save(any(TipoPlantacao.class))).thenReturn(updatedEntity);

        TipoPlantacaoResponse response = service.update(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Trigo", response.tipoPlant());
        verify(repository, times(1)).save(any(TipoPlantacao.class));
    }

    @Test
    void shouldThrowResourceNotFoundWhenUpdatingNonExistentTipoPlantacao() {
        TipoPlantacaoRequest request = new TipoPlantacaoRequest("Trigo");

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, request));
    }

    @Test
    void shouldDeleteTipoPlantacaoSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockTipoPlantacao));

        service.delete(1L);

        verify(repository, times(1)).delete(mockTipoPlantacao);
    }

    @Test
    void shouldThrowResourceNotFoundWhenDeletingNonExistentTipoPlantacao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }
}
