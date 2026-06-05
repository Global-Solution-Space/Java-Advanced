package fiap.com.br.terranova.dadotemporal;

import fiap.com.br.terranova.dadotemporal.dto.DadoTemporalResponse;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.reqapi.ReqApi;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import fiap.com.br.terranova.talhao.Talhao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DadoTemporalServiceTest {

    @Mock
    private DadoTemporalRepository repository;

    @InjectMocks
    private DadoTemporalService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFindAllDadosTemporais() {
        Talhao talhao = Talhao.builder().idTalhao(1L).build();
        ReqApi reqApi = ReqApi.builder()
                .idApi(10L)
                .tipoApi(TipoApi.builder().tipoApi("NASAPOWER").build())
                .tipoParam("PRECTOTCORR")
                .build();
        DadoTemporal dado = DadoTemporal.builder()
                .idDado(1L)
                .dataLeitura(LocalDate.of(2026, 1, 2))
                .valor(12.5)
                .talhao(talhao)
                .reqApi(reqApi)
                .build();

        when(repository.findAll()).thenReturn(List.of(dado));

        List<DadoTemporalResponse> responses = service.findAll();

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).idDado());
        assertEquals("NASAPOWER", responses.get(0).tipoApiNome());
        assertEquals("PRECTOTCORR", responses.get(0).tipoParam());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldReturnDadoTemporalWhenIdExists() {
        // Arrange
        Talhao talhao = Talhao.builder().idTalhao(1L).nomeTalhao("Talhao 1").build();
        ReqApi reqApi = ReqApi.builder()
                .idApi(10L)
                .tipoApi(TipoApi.builder().tipoApi("SATVEG").build())
                .tipoParam("NDVI")
                .build();
        
        DadoTemporal dado = DadoTemporal.builder()
                .idDado(5L)
                .dataLeitura(LocalDate.of(2026, 1, 1))
                .valor(0.75)
                .talhao(talhao)
                .reqApi(reqApi)
                .build();

        when(repository.findById(5L)).thenReturn(Optional.of(dado));

        // Act
        DadoTemporalResponse response = service.findById(5L);

        // Assert
        assertNotNull(response);
        assertEquals(5L, response.idDado());
        assertEquals(LocalDate.of(2026, 1, 1), response.dataLeitura());
        assertEquals(0.75, response.valor());
        assertEquals(1L, response.idTalhao());
        assertEquals(10L, response.idReqApi());
        assertEquals("SATVEG", response.tipoApiNome());
        assertEquals("NDVI", response.tipoParam());
        
        verify(repository, times(1)).findById(5L);
    }

    @Test
    void shouldThrowExceptionWhenDadoTemporalNotFound() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(99L);
        });
        
        assertTrue(exception.getMessage().contains("Dado temporal com id 99 não encontrado"));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void shouldReturnListByTalhao() {
        // Arrange
        Talhao talhao = Talhao.builder().idTalhao(2L).build();
        ReqApi reqApi = ReqApi.builder()
                .idApi(10L)
                .tipoApi(TipoApi.builder().tipoApi("NASA").build())
                .tipoParam("PRECIP")
                .build();
                
        DadoTemporal dado1 = DadoTemporal.builder().idDado(1L).talhao(talhao).reqApi(reqApi).valor(10.0).build();
        DadoTemporal dado2 = DadoTemporal.builder().idDado(2L).talhao(talhao).reqApi(reqApi).valor(15.0).build();

        when(repository.findByTalhaoIdTalhao(2L)).thenReturn(List.of(dado1, dado2));

        // Act
        List<DadoTemporalResponse> responses = service.findByTalhao(2L);

        // Assert
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).idDado());
        assertEquals(2L, responses.get(1).idDado());
        verify(repository, times(1)).findByTalhaoIdTalhao(2L);
    }

    @Test
    void shouldReturnListByReqApi() {
        Talhao talhao = Talhao.builder().idTalhao(2L).build();
        ReqApi reqApi = ReqApi.builder()
                .idApi(20L)
                .tipoApi(TipoApi.builder().tipoApi("SATVEG").build())
                .tipoParam("NDVI")
                .build();
        DadoTemporal dado = DadoTemporal.builder()
                .idDado(3L)
                .talhao(talhao)
                .reqApi(reqApi)
                .valor(0.62)
                .build();

        when(repository.findByReqApiIdApi(20L)).thenReturn(List.of(dado));

        List<DadoTemporalResponse> responses = service.findByReqApi(20L);

        assertEquals(1, responses.size());
        assertEquals(3L, responses.get(0).idDado());
        assertEquals(20L, responses.get(0).idReqApi());
        assertEquals("SATVEG", responses.get(0).tipoApiNome());
        verify(repository, times(1)).findByReqApiIdApi(20L);
    }
}
