package fiap.com.br.terranova.propriedade;

import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.terranova.exception.GlobalExceptionHandler;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.propriedade.dto.PropriedadeRequest;
import fiap.com.br.terranova.propriedade.dto.PropriedadeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import fiap.com.br.terranova.validation.ValidLocalizacaoDisponibilidadeValidator;
import fiap.com.br.terranova.validation.ValidPropriedadeAreaValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.HandlerMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class PropriedadeControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PropriedadeService service;

    @Mock
    private TalhaoRepository talhaoRepository;

    @Mock
    private PropriedadeRepository propriedadeRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PropriedadeController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ConstraintValidatorFactory validatorFactory = new ConstraintValidatorFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == ValidPropriedadeAreaValidator.class) {
                    return (T) new ValidPropriedadeAreaValidator(talhaoRepository, request);
                }
                if (key == ValidLocalizacaoDisponibilidadeValidator.class) {
                    return (T) new ValidLocalizacaoDisponibilidadeValidator(propriedadeRepository, talhaoRepository, request);
                }
                try {
                    return key.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void releaseInstance(ConstraintValidator<?, ?> instance) {
            }
        };

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setConstraintValidatorFactory(validatorFactory);
        validator.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldReturnBadRequestWhenCreatingPropriedadeWithInvalidData() throws Exception {
        // Nome em branco, tamanho total negativo, tamanho excedendo 10000, idProdutor null, idLocalizacao null
        PropriedadeRequest request = new PropriedadeRequest("", -10.0, null, null);

        mockMvc.perform(post("/api/propriedades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    void shouldCreatePropriedadeSuccessfullyWhenDataIsValid() throws Exception {
        PropriedadeRequest request = new PropriedadeRequest("Fazenda Sol", 500.0, 1L, 2L);
        PropriedadeResponse response = new PropriedadeResponse(10L, "Fazenda Sol", 500.0, 1L, "Enzo", 2L);

        when(service.create(any(PropriedadeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/propriedades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Fazenda Sol"))
                .andExpect(jsonPath("$.tamanhoTotal").value(500.0));
    }

    @Test
    void shouldReturnBadRequestWhenLocalizacaoAlreadyUsed() throws Exception {
        PropriedadeRequest request = new PropriedadeRequest("Fazenda Sol", 500.0, 1L, 2L);
        when(propriedadeRepository.existsByLocalizacaoIdLocalizacao(2L)).thenReturn(true);

        mockMvc.perform(post("/api/propriedades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());

        verify(service, never()).create(any(PropriedadeRequest.class));
    }

    @Test
    void shouldReturnPropriedadeWhenFoundById() throws Exception {
        PropriedadeResponse response = new PropriedadeResponse(10L, "Fazenda Sol", 500.0, 1L, "Enzo", 2L);
        when(service.findById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/propriedades/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fazenda Sol"));
    }

    @Test
    void shouldReturnNotFoundWhenPropriedadeDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Propriedade com id 99 nao encontrada."));

        mockMvc.perform(get("/api/propriedades/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Propriedade com id 99 nao encontrada."));
    }

    @Test
    void shouldFindPropriedadesByProdutorId() throws Exception {
        PropriedadeResponse response = new PropriedadeResponse(10L, "Fazenda Sol", 500.0, 1L, "Enzo", 2L);
        when(service.findByProdutor(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/propriedades/produtor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Fazenda Sol"));
    }

    @Test
    void shouldUpdatePropriedadeSuccessfully() throws Exception {
        PropriedadeRequest requestData = new PropriedadeRequest("Fazenda Sol Novo", 600.0, 1L, 2L);
        PropriedadeResponse response = new PropriedadeResponse(10L, "Fazenda Sol Novo", 600.0, 1L, "Enzo", 2L);

        when(service.update(eq(10L), any(PropriedadeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/propriedades/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fazenda Sol Novo"))
                .andExpect(jsonPath("$.tamanhoTotal").value(600.0));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingPropriedadeWithTamanhoTotalSmallerThanTalhoesAreaSum() throws Exception {
        PropriedadeRequest requestData = new PropriedadeRequest("Fazenda Sol", 100.0, 1L, 2L);

        // Simular que o talhaoRepository retorna talhões com soma de área = 150.0 (maior que os 100.0 do request)
        Talhao talhaoExistente = Talhao.builder().idTalhao(10L).volumArea(150.0).build();
        when(talhaoRepository.findByPropriedadeIdPropriedade(10L)).thenReturn(List.of(talhaoExistente));

        // Mockar path variable "id" na requisição
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "10");
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);

        mockMvc.perform(put("/api/propriedades/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    void shouldDeletePropriedadeSuccessfully() throws Exception {
        doNothing().when(service).delete(10L);

        mockMvc.perform(delete("/api/propriedades/10"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(10L);
    }
}
