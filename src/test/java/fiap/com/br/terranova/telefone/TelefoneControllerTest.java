package fiap.com.br.terranova.telefone;

import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.terranova.exception.GlobalExceptionHandler;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.telefone.dto.TelefoneRequest;
import fiap.com.br.terranova.telefone.dto.TelefoneResponse;
import fiap.com.br.terranova.validation.UniqueTelefoneValidator;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TelefoneControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TelefoneService service;

    @Mock
    private TelefoneRepository telefoneRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TelefoneController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ConstraintValidatorFactory validatorFactory = new ConstraintValidatorFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == UniqueTelefoneValidator.class) {
                    return (T) new UniqueTelefoneValidator(telefoneRepository, request);
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
    void shouldReturnBadRequestWhenCreatingTelefoneWithInvalidData() throws Exception {
        // DDD vazio, numero com letras e sem ddd valido
        TelefoneRequest invalidRequest = new TelefoneRequest("", "numero123", null);

        mockMvc.perform(post("/api/telefones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    void shouldReturnBadRequestWhenDddIsTooLongOrShort() throws Exception {
        TelefoneRequest invalidRequest = new TelefoneRequest("111", "999999999", 1L);

        mockMvc.perform(post("/api/telefones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    void shouldCreateTelefoneSuccessfullyWhenDataIsValid() throws Exception {
        TelefoneRequest request = new TelefoneRequest("11", "999999999", 1L);
        TelefoneResponse response = new TelefoneResponse(10L, "11", "999999999", 1L);

        when(service.create(any(TelefoneRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/telefones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ddd").value("11"))
                .andExpect(jsonPath("$.numero").value("999999999"));
    }

    @Test
    void shouldReturnTelefoneWhenFoundById() throws Exception {
        TelefoneResponse response = new TelefoneResponse(10L, "11", "999999999", 1L);
        when(service.findById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/telefones/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ddd").value("11"))
                .andExpect(jsonPath("$.numero").value("999999999"));
    }

    @Test
    void shouldReturnNotFoundWhenTelefoneDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Telefone com id 99 não encontrado."));

        mockMvc.perform(get("/api/telefones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Telefone com id 99 não encontrado."));
    }

    @Test
    void shouldFindAllTelefones() throws Exception {
        TelefoneResponse response = new TelefoneResponse(10L, "11", "999999999", 1L);
        Page<TelefoneResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/telefones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ddd").value("11"));
    }

    @Test
    void shouldUpdateTelefoneSuccessfully() throws Exception {
        TelefoneRequest request = new TelefoneRequest("21", "888888888", 1L);
        TelefoneResponse response = new TelefoneResponse(10L, "21", "888888888", 1L);

        when(service.update(eq(10L), any(TelefoneRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/telefones/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ddd").value("21"))
                .andExpect(jsonPath("$.numero").value("888888888"));
    }

    @Test
    void shouldDeleteTelefoneSuccessfully() throws Exception {
        doNothing().when(service).delete(10L);

        mockMvc.perform(delete("/api/telefones/10"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(10L);
    }
}
