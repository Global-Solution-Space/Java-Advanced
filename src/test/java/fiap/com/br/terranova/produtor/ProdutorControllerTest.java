package fiap.com.br.terranova.produtor;

import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.terranova.exception.GlobalExceptionHandler;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.produtor.dto.ProdutorRequest;
import fiap.com.br.terranova.produtor.dto.ProdutorResponse;
import fiap.com.br.terranova.validation.UniqueEmailValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ProdutorControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProdutorService service;

    @Mock
    private ProdutorRepository produtorRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ProdutorController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configura um validator factory personalizado para injetar os mocks no UniqueEmailValidator
        ConstraintValidatorFactory validatorFactory = new ConstraintValidatorFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == UniqueEmailValidator.class) {
                    return (T) new UniqueEmailValidator(produtorRepository, request);
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
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldReturnBadRequestWhenCreatingProdutorWithInvalidData() throws Exception {
        // Nome em branco, email mal formatado e senha muito curta
        ProdutorRequest request = new ProdutorRequest("", "emailinvalido", "123", null);

        mockMvc.perform(post("/api/produtores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    void shouldReturnBadRequestWhenEmailAlreadyInUse() throws Exception {
        // Mock do repository para dizer que o e-mail já existe
        when(produtorRepository.existsByEmail("enzo@fiap.com")).thenReturn(true);

        ProdutorRequest request = new ProdutorRequest("Enzo", "enzo@fiap.com", "senha123", null);

        mockMvc.perform(post("/api/produtores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray())
                .andExpect(jsonPath("$.erros[0].mensagem").value("O e-mail informado ja esta em uso no sistema."));
    }

    @Test
    void shouldCreateProdutorSuccessfullyWhenDataIsValid() throws Exception {
        ProdutorRequest request = new ProdutorRequest("Enzo", "enzo@fiap.com", "senha123", null);
        ProdutorResponse response = new ProdutorResponse(1L, "Enzo", "enzo@fiap.com", null);

        // E-mail não está em uso
        when(produtorRepository.existsByEmail("enzo@fiap.com")).thenReturn(false);
        when(service.create(any(ProdutorRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/produtores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Enzo"))
                .andExpect(jsonPath("$.email").value("enzo@fiap.com"));
    }

    @Test
    void shouldReturnProdutorWhenFoundById() throws Exception {
        ProdutorResponse response = new ProdutorResponse(1L, "Enzo", "enzo@fiap.com", null);
        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/produtores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Enzo"))
                .andExpect(jsonPath("$.email").value("enzo@fiap.com"));
    }

    @Test
    void shouldReturnNotFoundWhenProdutorDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Produtor com id 99 nao encontrado."));

        mockMvc.perform(get("/api/produtores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Produtor com id 99 nao encontrado."));
    }

    @Test
    void shouldUpdateProdutorSuccessfully() throws Exception {
        ProdutorRequest requestData = new ProdutorRequest("Enzo Alterado", "enzo.novo@fiap.com", "novasenha123", null);
        ProdutorResponse response = new ProdutorResponse(1L, "Enzo Alterado", "enzo.novo@fiap.com", null);

        // Simula path variables do HttpServletRequest para que o validador passe se for o mesmo id
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "1");
        when(request.getAttribute(any(String.class))).thenReturn(pathVariables);
        
        when(produtorRepository.existsByEmail("enzo.novo@fiap.com")).thenReturn(false);
        when(service.update(eq(1L), any(ProdutorRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/produtores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Enzo Alterado"))
                .andExpect(jsonPath("$.email").value("enzo.novo@fiap.com"));
    }

    @Test
    void shouldDeleteProdutorSuccessfully() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/produtores/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1L);
    }
}
