package fiap.com.br.terranova.tipoplantacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.terranova.exception.GlobalExceptionHandler;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TipoPlantacaoControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TipoPlantacaoService service;

    @InjectMocks
    private TipoPlantacaoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void shouldReturnBadRequestWhenCreatingTipoPlantacaoWithInvalidData() throws Exception {
        // Nome em branco
        TipoPlantacaoRequest invalidRequest = new TipoPlantacaoRequest("");

        mockMvc.perform(post("/api/tipos-plantacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    void shouldReturnBadRequestWhenTipoPlantacaoNameIsTooLong() throws Exception {
        // Mais de 30 caracteres
        TipoPlantacaoRequest invalidRequest = new TipoPlantacaoRequest("EsseNomeDeTipoDePlantacaoTemMaisDeTrintaCaracteres");

        mockMvc.perform(post("/api/tipos-plantacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    void shouldCreateTipoPlantacaoSuccessfullyWhenDataIsValid() throws Exception {
        TipoPlantacaoRequest request = new TipoPlantacaoRequest("Algodão");
        TipoPlantacaoResponse response = new TipoPlantacaoResponse(10L, "Algodão");

        when(service.create(any(TipoPlantacaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tipos-plantacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoPlant").value("Algodão"));
    }

    @Test
    void shouldReturnTipoPlantacaoWhenFoundById() throws Exception {
        TipoPlantacaoResponse response = new TipoPlantacaoResponse(10L, "Algodão");
        when(service.findById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/tipos-plantacao/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoPlant").value("Algodão"));
    }

    @Test
    void shouldReturnNotFoundWhenTipoPlantacaoDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("TipoPlantacao com id 99 não encontrado."));

        mockMvc.perform(get("/api/tipos-plantacao/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("TipoPlantacao com id 99 não encontrado."));
    }

    @Test
    void shouldFindAllTiposPlantacao() throws Exception {
        TipoPlantacaoResponse response = new TipoPlantacaoResponse(10L, "Algodão");
        Page<TipoPlantacaoResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/tipos-plantacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].tipoPlant").value("Algodão"));
    }

    @Test
    void shouldUpdateTipoPlantacaoSuccessfully() throws Exception {
        TipoPlantacaoRequest request = new TipoPlantacaoRequest("Cana de Açúcar");
        TipoPlantacaoResponse response = new TipoPlantacaoResponse(10L, "Cana de Açúcar");

        when(service.update(eq(10L), any(TipoPlantacaoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/tipos-plantacao/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoPlant").value("Cana de Açúcar"));
    }

    @Test
    void shouldDeleteTipoPlantacaoSuccessfully() throws Exception {
        doNothing().when(service).delete(10L);

        mockMvc.perform(delete("/api/tipos-plantacao/10"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(10L);
    }
}
