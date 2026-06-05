package fiap.com.br.terranova.alerta;

import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.terranova.alerta.dto.AlertaRequest;
import fiap.com.br.terranova.alerta.dto.AlertaResponse;
import fiap.com.br.terranova.exception.GlobalExceptionHandler;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AlertaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AlertaService service;

    @InjectMocks
    private AlertaController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldFindByIdSuccessfully() throws Exception {
        AlertaResponse response = new AlertaResponse(1L, "Titulo", "Desc", "ALTO", "N",
                new Timestamp(System.currentTimeMillis()), 1L);
        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/alertas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Alerta com id 99 não encontrado."));

        mockMvc.perform(get("/api/alertas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindByProdutor() throws Exception {
        AlertaResponse response = new AlertaResponse(1L, "Titulo", "Desc", "ALTO", "N",
                new Timestamp(System.currentTimeMillis()), 1L);
        when(service.findByProdutor(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/alertas/produtor/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateAlerta() throws Exception {
        AlertaRequest request = new AlertaRequest("Titulo", "Desc", "ALTO", "N", 1L);
        AlertaResponse response = new AlertaResponse(1L, "Titulo", "Desc", "ALTO", "N",
                new Timestamp(System.currentTimeMillis()), 1L);

        when(service.create(any(AlertaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/alertas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Titulo"));
    }

    @Test
    void shouldUpdateAlerta() throws Exception {
        AlertaRequest request = new AlertaRequest("Titulo Novo", "Desc", "ALTO", "N", 1L);
        AlertaResponse response = new AlertaResponse(1L, "Titulo Novo", "Desc", "ALTO", "N",
                new Timestamp(System.currentTimeMillis()), 1L);

        when(service.update(eq(1L), any(AlertaRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/alertas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Titulo Novo"));
    }

    @Test
    void shouldResolverAlerta() throws Exception {
        AlertaResponse response = new AlertaResponse(1L, "Titulo", "Desc", "ALTO", "S",
                new Timestamp(System.currentTimeMillis()), 1L);
        when(service.resolver(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/alertas/1/resolver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolvido").value("S"));
    }

    @Test
    void shouldReabrirAlerta() throws Exception {
        AlertaResponse response = new AlertaResponse(1L, "Titulo", "Desc", "ALTO", "N",
                new Timestamp(System.currentTimeMillis()), 1L);
        when(service.reabrir(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/alertas/1/reabrir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolvido").value("N"));
    }

    @Test
    void shouldDeleteAlerta() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/alertas/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1L);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingAlertaWithInvalidData() throws Exception {
        AlertaRequest invalidRequest = new AlertaRequest("", "Desc", "INVALIDO", "Z", null);

        mockMvc.perform(post("/api/alertas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }
}
