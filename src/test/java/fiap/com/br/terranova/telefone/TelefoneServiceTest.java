package fiap.com.br.terranova.telefone;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.produtor.ProdutorRepository;
import fiap.com.br.terranova.telefone.dto.TelefoneRequest;
import fiap.com.br.terranova.telefone.dto.TelefoneResponse;
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

class TelefoneServiceTest {

    @Mock
    private TelefoneRepository telefoneRepository;

    @Mock
    private ProdutorRepository produtorRepository;

    @InjectMocks
    private TelefoneService service;

    private Produtor mockProdutor;
    private Telefone mockTelefone;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockProdutor = Produtor.builder().idProdutor(1L).nome("Enzo").email("enzo@fiap.com").build();
        mockTelefone = Telefone.builder()
                .idTelefone(10L)
                .ddd("11")
                .numero("999999999")
                .produtor(mockProdutor)
                .build();
    }

    @Test
    void shouldFindAllTelefones() {
        Page<Telefone> page = new PageImpl<>(List.of(mockTelefone));
        when(telefoneRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<TelefoneResponse> result = service.findAll(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("11", result.getContent().get(0).ddd());
        assertEquals("999999999", result.getContent().get(0).numero());
    }

    @Test
    void shouldFindTelefoneById() {
        when(telefoneRepository.findById(10L)).thenReturn(Optional.of(mockTelefone));

        TelefoneResponse response = service.findById(10L);

        assertNotNull(response);
        assertEquals(10L, response.id());
    }

    @Test
    void shouldThrowResourceNotFoundWhenTelefoneDoesNotExist() {
        when(telefoneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void shouldCreateTelefoneSuccessfully() {
        TelefoneRequest request = new TelefoneRequest("11", "999999999", 1L);

        when(produtorRepository.findById(1L)).thenReturn(Optional.of(mockProdutor));
        when(telefoneRepository.save(any(Telefone.class))).thenReturn(mockTelefone);

        TelefoneResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("11", response.ddd());
        assertEquals("999999999", response.numero());
        verify(telefoneRepository, times(1)).save(any(Telefone.class));
    }

    @Test
    void shouldThrowResourceNotFoundWhenCreatingWithInvalidProdutor() {
        TelefoneRequest request = new TelefoneRequest("11", "999999999", 99L);

        when(produtorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void shouldUpdateTelefoneSuccessfully() {
        TelefoneRequest request = new TelefoneRequest("21", "888888888", 1L);
        Telefone updatedTelefone = Telefone.builder()
                .idTelefone(10L)
                .ddd("21")
                .numero("888888888")
                .produtor(mockProdutor)
                .build();

        when(telefoneRepository.findById(10L)).thenReturn(Optional.of(mockTelefone));
        when(produtorRepository.findById(1L)).thenReturn(Optional.of(mockProdutor));
        when(telefoneRepository.save(any(Telefone.class))).thenReturn(updatedTelefone);

        TelefoneResponse response = service.update(10L, request);

        assertNotNull(response);
        assertEquals("21", response.ddd());
        assertEquals("888888888", response.numero());
        verify(telefoneRepository, times(1)).save(any(Telefone.class));
    }

    @Test
    void shouldThrowResourceNotFoundWhenUpdatingNonExistentTelefone() {
        TelefoneRequest request = new TelefoneRequest("21", "888888888", 1L);

        when(telefoneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, request));
    }

    @Test
    void shouldDeleteTelefoneSuccessfully() {
        when(telefoneRepository.findById(10L)).thenReturn(Optional.of(mockTelefone));

        service.delete(10L);

        verify(telefoneRepository, times(1)).delete(mockTelefone);
    }

    @Test
    void shouldThrowResourceNotFoundWhenDeletingNonExistentTelefone() {
        when(telefoneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }
}
