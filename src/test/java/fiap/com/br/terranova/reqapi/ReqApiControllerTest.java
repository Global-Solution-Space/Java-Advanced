package fiap.com.br.terranova.reqapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.terranova.exception.GlobalExceptionHandler;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.reqapi.dto.ReqApiRequest;
import fiap.com.br.terranova.reqapi.dto.ReqApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReqApiControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ReqApiService service;

    @InjectMocks
    private ReqApiController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void shouldReturnBadRequestWhenCreatingReqApiWithInvalidData() throws Exception {
        // Envia tipoParam invalido, tipoApiNome invalido, e idTalhao null
        ReqApiRequest request = new ReqApiRequest("INVALID_PARAM", "INVALID_API", null);

        mockMvc.perform(post("/api/req-api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    void shouldCreateReqApiSuccessfullyWhenDataIsValid() throws Exception {
        ReqApiRequest request = new ReqApiRequest("NDVI", "SATVEG", 1L);
        ReqApiResponse response = new ReqApiResponse(10L, "SATVEG", "NDVI", new Timestamp(System.currentTimeMillis()), 2L, 50);

        when(service.create(any(ReqApiRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/req-api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoApiNome").value("SATVEG"))
                .andExpect(jsonPath("$.tipoParam").value("NDVI"));
    }

    @Test
    void shouldReturnReqApiWhenFoundById() throws Exception {
        ReqApiResponse response = new ReqApiResponse(10L, "SATVEG", "NDVI", new Timestamp(System.currentTimeMillis()), 2L, 50);
        when(service.findById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/req-api/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoApiNome").value("SATVEG"));
    }

    @Test
    void shouldReturnNotFoundWhenReqApiDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("ReqApi com id 99 nao encontrada."));

        mockMvc.perform(get("/api/req-api/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("ReqApi com id 99 nao encontrada."));
    }

    @Test
    void shouldFindAllReqApis() throws Exception {
        ReqApiResponse response = new ReqApiResponse(10L, "SATVEG", "NDVI", new Timestamp(System.currentTimeMillis()), 2L, 50);
        Page<ReqApiResponse> page = new PageImpl<>(List.of(response), org.springframework.data.domain.PageRequest.of(0, 10), 1);
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/req-api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].tipoApiNome").value("SATVEG"));
    }

    @Test
    void shouldFindReqApisByTalhaoId() throws Exception {
        ReqApiResponse response = new ReqApiResponse(10L, "SATVEG", "NDVI", new Timestamp(System.currentTimeMillis()), 2L, 50);
        Page<ReqApiResponse> page = new PageImpl<>(List.of(response), org.springframework.data.domain.PageRequest.of(0, 10), 1);
        when(service.findByTalhaoId(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/req-api/talhao/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].tipoApiNome").value("SATVEG"));
    }

    @Test
    void shouldDeleteReqApiSuccessfully() throws Exception {
        doNothing().when(service).delete(10L);

        mockMvc.perform(delete("/api/req-api/10"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(10L);
    }
}
