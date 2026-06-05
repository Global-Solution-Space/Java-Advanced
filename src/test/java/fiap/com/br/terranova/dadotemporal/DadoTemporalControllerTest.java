package fiap.com.br.terranova.dadotemporal;

import fiap.com.br.terranova.dadotemporal.dto.DadoTemporalResponse;
import fiap.com.br.terranova.exception.GlobalExceptionHandler;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DadoTemporalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DadoTemporalService service;

    @InjectMocks
    private DadoTemporalController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldFindAllDadosTemporais() throws Exception {
        DadoTemporalResponse response = createResponse();
        when(service.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/dados-temporais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idDado").value(5))
                .andExpect(jsonPath("$.content[0].tipoParam").value("NDVI"))
                .andExpect(jsonPath("$.links[0].rel").value("self"));

        verify(service, times(1)).findAll();
    }

    @Test
    void shouldFindDadoTemporalById() throws Exception {
        DadoTemporalResponse response = createResponse();
        when(service.findById(5L)).thenReturn(response);

        mockMvc.perform(get("/api/dados-temporais/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDado").value(5))
                .andExpect(jsonPath("$.valor").value(0.75))
                .andExpect(jsonPath("$.tipoApiNome").value("SATVEG"))
                .andExpect(jsonPath("$.links[0].rel").value("self"));

        verify(service, times(1)).findById(5L);
    }

    @Test
    void shouldReturnNotFoundWhenDadoTemporalDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Dado temporal com id 99 nao encontrado"));

        mockMvc.perform(get("/api/dados-temporais/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Dado temporal com id 99 nao encontrado"));

        verify(service, times(1)).findById(99L);
    }

    @Test
    void shouldFindDadosTemporaisByTalhao() throws Exception {
        DadoTemporalResponse response = createResponse();
        when(service.findByTalhao(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/dados-temporais/talhao/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idTalhao").value(1))
                .andExpect(jsonPath("$.content[0].tipoApiNome").value("SATVEG"))
                .andExpect(jsonPath("$.links[0].rel").value("self"));

        verify(service, times(1)).findByTalhao(1L);
    }

    @Test
    void shouldFindDadosTemporaisByReqApi() throws Exception {
        DadoTemporalResponse response = createResponse();
        when(service.findByReqApi(10L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/dados-temporais/req-api/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idReqApi").value(10))
                .andExpect(jsonPath("$.content[0].tipoParam").value("NDVI"))
                .andExpect(jsonPath("$.links[0].rel").value("self"));

        verify(service, times(1)).findByReqApi(10L);
    }

    private DadoTemporalResponse createResponse() {
        return new DadoTemporalResponse(
                5L,
                LocalDate.of(2026, 1, 1),
                0.75,
                1L,
                10L,
                "SATVEG",
                "NDVI"
        );
    }
}
