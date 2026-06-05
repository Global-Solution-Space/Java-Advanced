package fiap.com.br.terranova.produtor;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.produtor.dto.ProdutorRequest;
import fiap.com.br.terranova.produtor.dto.ProdutorResponse;
import fiap.com.br.terranova.telefone.Telefone;
import fiap.com.br.terranova.telefone.TelefoneRepository;
import fiap.com.br.terranova.telefone.dto.TelefoneRequest;
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

class ProdutorServiceTest {

    @Mock
    private ProdutorRepository produtorRepository;

    @Mock
    private TelefoneRepository telefoneRepository;

    @InjectMocks
    private ProdutorService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFindAllProdutores() {
        // Arrange
        Produtor produtor = Produtor.builder().idProdutor(1L).nome("Enzo").email("enzo@fiap.com").build();
        Page<Produtor> page = new PageImpl<>(List.of(produtor));
        when(produtorRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<ProdutorResponse> result = service.findAll(Pageable.unpaged());

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Enzo", result.getContent().get(0).nome());
        verify(produtorRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldFindProdutorById() {
        // Arrange
        Produtor produtor = Produtor.builder().idProdutor(1L).nome("Enzo").email("enzo@fiap.com").build();
        when(produtorRepository.findById(1L)).thenReturn(Optional.of(produtor));

        // Act
        ProdutorResponse response = service.findById(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Enzo", response.nome());
        verify(produtorRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProdutorNotFoundById() {
        // Arrange
        when(produtorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void shouldCreateProdutorWithTelefone() {
        // Arrange
        TelefoneRequest telRequest = new TelefoneRequest("11", "999999999", null);
        ProdutorRequest request = new ProdutorRequest("Enzo", "enzo@fiap.com", "senha123", telRequest);
        
        Produtor savedProdutor = Produtor.builder().idProdutor(1L).nome("Enzo").email("enzo@fiap.com").build();
        when(produtorRepository.save(any(Produtor.class))).thenReturn(savedProdutor);

        // Act
        ProdutorResponse response = service.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("Enzo", response.nome());
        verify(produtorRepository, times(1)).save(any(Produtor.class));
        verify(telefoneRepository, times(1)).save(any(Telefone.class));
    }

    @Test
    void shouldCreateProdutorWithoutTelefone() {
        ProdutorRequest request = new ProdutorRequest("Enzo", "enzo@fiap.com", "senha123", null);
        Produtor savedProdutor = Produtor.builder()
                .idProdutor(1L)
                .nome("Enzo")
                .email("enzo@fiap.com")
                .build();
        when(produtorRepository.save(any(Produtor.class))).thenReturn(savedProdutor);

        ProdutorResponse response = service.create(request);

        assertEquals("Enzo", response.nome());
        verify(produtorRepository, times(1)).save(any(Produtor.class));
        verify(telefoneRepository, never()).save(any(Telefone.class));
    }

    @Test
    void shouldUpdateProdutor() {
        // Arrange
        ProdutorRequest request = new ProdutorRequest("Enzo Silva", "enzo@fiap.com", "novasenha", null);
        Produtor existingProdutor = Produtor.builder().idProdutor(1L).nome("Enzo").email("enzo@fiap.com").build();
        
        when(produtorRepository.findById(1L)).thenReturn(Optional.of(existingProdutor));
        when(produtorRepository.save(any(Produtor.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ProdutorResponse response = service.update(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals("Enzo Silva", response.nome());
        verify(produtorRepository, times(1)).save(any(Produtor.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProdutor() {
        ProdutorRequest request = new ProdutorRequest("Enzo Silva", "enzo@fiap.com", "novasenha", null);
        when(produtorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, request));
        verify(produtorRepository, never()).save(any(Produtor.class));
    }

    @Test
    void shouldDeleteProdutor() {
        // Arrange
        Produtor existingProdutor = Produtor.builder().idProdutor(1L).nome("Enzo").build();
        when(produtorRepository.findById(1L)).thenReturn(Optional.of(existingProdutor));

        // Act
        service.delete(1L);

        // Assert
        verify(produtorRepository, times(1)).delete(existingProdutor);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProdutor() {
        when(produtorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(produtorRepository, never()).delete(any(Produtor.class));
    }
}
