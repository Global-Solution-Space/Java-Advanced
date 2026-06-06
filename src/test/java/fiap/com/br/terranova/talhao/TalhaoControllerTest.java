package fiap.com.br.terranova.talhao;

import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.terranova.exception.GlobalExceptionHandler;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.propriedade.Propriedade;
import fiap.com.br.terranova.propriedade.PropriedadeRepository;
import fiap.com.br.terranova.talhao.dto.TalhaoRequest;
import fiap.com.br.terranova.talhao.dto.TalhaoResponse;
import fiap.com.br.terranova.validation.ValidLocalizacaoDisponibilidadeValidator;
import fiap.com.br.terranova.validation.ValidTalhaoAreaValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TalhaoControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TalhaoService service;

    @Mock
    private TalhaoRepository talhaoRepository;

    @Mock
    private PropriedadeRepository propriedadeRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TalhaoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ConstraintValidatorFactory validatorFactory = new ConstraintValidatorFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == ValidTalhaoAreaValidator.class) {
                    return (T) new ValidTalhaoAreaValidator(talhaoRepository, propriedadeRepository, request);
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
    void shouldReturnBadRequestWhenCreatingTalhaoWithInvalidRequest() throws Exception {
        // Nome vazio, volume/area negativo, IDs nulos
        TalhaoRequest invalidRequest = new TalhaoRequest("", -5.0, null, null, null);

        mockMvc.perform(post("/api/talhoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    void shouldReturnBadRequestWhenTalhaoAreaExceedsPropriedadeTotalArea() throws Exception {
        // A propriedade tem 100.0 hectares no total
        Propriedade propriedade = Propriedade.builder().idPropriedade(1L).tamanhoTotal(100.0).build();
        when(propriedadeRepository.findById(1L)).thenReturn(Optional.of(propriedade));

        // Talhões existentes somam 90.0 hectares
        Talhao talhaoExistente = Talhao.builder().idTalhao(2L).volumArea(90.0).build();
        when(talhaoRepository.findByPropriedadeIdPropriedade(1L)).thenReturn(List.of(talhaoExistente));

        // Nova requisição tenta cadastrar um talhão de 20.0 hectares (90 + 20 = 110 > 100)
        TalhaoRequest requestDto = new TalhaoRequest("Talhao C", 20.0, 1L, 1L, 1L);

        mockMvc.perform(post("/api/talhoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    void shouldCreateTalhaoSuccessfullyWhenDataIsValid() throws Exception {
        Propriedade propriedade = Propriedade.builder().idPropriedade(1L).tamanhoTotal(100.0).build();
        when(propriedadeRepository.findById(1L)).thenReturn(Optional.of(propriedade));
        when(talhaoRepository.findByPropriedadeIdPropriedade(1L)).thenReturn(List.of());

        TalhaoRequest requestDto = new TalhaoRequest("Talhao Novo", 45.0, 1L, 1L, 1L);
        TalhaoResponse response = new TalhaoResponse(10L, "Talhao Novo", 45.0, 1L, "Milho", 1L, "Fazenda Milho", 1L);

        when(service.create(any(TalhaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/talhoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeTalhao").value("Talhao Novo"))
                .andExpect(jsonPath("$.volumArea").value(45.0));
    }

    @Test
    void shouldReturnBadRequestWhenLocalizacaoAlreadyUsed() throws Exception {
        Propriedade propriedade = Propriedade.builder().idPropriedade(1L).tamanhoTotal(100.0).build();
        when(propriedadeRepository.findById(1L)).thenReturn(Optional.of(propriedade));
        when(talhaoRepository.findByPropriedadeIdPropriedade(1L)).thenReturn(List.of());
        when(talhaoRepository.existsByLocalizacaoIdLocalizacao(1L)).thenReturn(true);

        TalhaoRequest requestDto = new TalhaoRequest("Talhao Novo", 45.0, 1L, 1L, 1L);

        mockMvc.perform(post("/api/talhoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());

        verify(service, never()).create(any(TalhaoRequest.class));
    }

    @Test
    void shouldReturnTalhaoWhenFoundById() throws Exception {
        TalhaoResponse response = new TalhaoResponse(10L, "Talhao A", 25.0, 1L, "Milho", 1L, "Fazenda Milho", 1L);
        when(service.findById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/talhoes/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeTalhao").value("Talhao A"));
    }

    @Test
    void shouldReturnNotFoundWhenTalhaoDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Talhão com id 99 não encontrado."));

        mockMvc.perform(get("/api/talhoes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Talhão com id 99 não encontrado."));
    }

    @Test
    void shouldFindAllTalhoes() throws Exception {
        TalhaoResponse response = new TalhaoResponse(10L, "Talhao A", 25.0, 1L, "Milho", 1L, "Fazenda Milho", 1L);
        Page<TalhaoResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/talhoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nomeTalhao").value("Talhao A"));
    }

    @Test
    void shouldFindTalhoesByProdutorId() throws Exception {
        TalhaoResponse response = new TalhaoResponse(10L, "Talhao A", 25.0, 1L, "Milho", 1L, "Fazenda Milho", 1L);
        when(service.findByProdutor(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/talhoes/produtor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nomeTalhao").value("Talhao A"));
    }

    @Test
    void shouldUpdateTalhaoSuccessfully() throws Exception {
        Propriedade propriedade = Propriedade.builder().idPropriedade(1L).tamanhoTotal(100.0).build();
        when(propriedadeRepository.findById(1L)).thenReturn(Optional.of(propriedade));
        
        // Simular a exclusão do id atual nas variáveis de path
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "10");
        when(request.getAttribute(any(String.class))).thenReturn(pathVariables);

        // O próprio talhão a ser editado tem 30.0 hectares cadastrados originalmente, nova área é 40.0
        Talhao talhaoOriginal = Talhao.builder().idTalhao(10L).volumArea(30.0).build();
        when(talhaoRepository.findByPropriedadeIdPropriedade(1L)).thenReturn(List.of(talhaoOriginal));

        TalhaoRequest requestDto = new TalhaoRequest("Talhao Editado", 40.0, 1L, 1L, 1L);
        TalhaoResponse response = new TalhaoResponse(10L, "Talhao Editado", 40.0, 1L, "Milho", 1L, "Fazenda Milho", 1L);

        when(service.update(eq(10L), any(TalhaoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/talhoes/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeTalhao").value("Talhao Editado"))
                .andExpect(jsonPath("$.volumArea").value(40.0));
    }

    @Test
    void shouldDeleteTalhaoSuccessfully() throws Exception {
        doNothing().when(service).delete(10L);

        mockMvc.perform(delete("/api/talhoes/10"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(10L);
    }
}
