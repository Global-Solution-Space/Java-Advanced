package fiap.com.br.terranova.localizacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.terranova.exception.GlobalExceptionHandler;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.localizacao.dto.LocalizacaoRequest;
import fiap.com.br.terranova.localizacao.dto.LocalizacaoResponse;
import fiap.com.br.terranova.validation.BrasilCoordenadasValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LocalizacaoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private LocalizacaoService service;

    @InjectMocks
    private LocalizacaoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ConstraintValidatorFactory validatorFactory = new ConstraintValidatorFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == BrasilCoordenadasValidator.class) {
                    return (T) new BrasilCoordenadasValidator() {
                        @Override
                        public boolean isValid(LocalizacaoRequest request, ConstraintValidatorContext context) {
                            return true;
                        }
                    };
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
    void shouldFindAllLocalizacoes() throws Exception {
        LocalizacaoResponse response = createResponse();
        Page<LocalizacaoResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/localizacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].locLatitude").value(-23.5505))
                .andExpect(jsonPath("$.content[0].locLongitude").value(-46.6333));

        verify(service, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldFindLocalizacaoById() throws Exception {
        LocalizacaoResponse response = createResponse();
        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/localizacoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.locLatitude").value(-23.5505))
                .andExpect(jsonPath("$.links[0].rel").value("self"));

        verify(service, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenLocalizacaoDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Localizacao com id 99 nao encontrada."));

        mockMvc.perform(get("/api/localizacoes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Localizacao com id 99 nao encontrada."));

        verify(service, times(1)).findById(99L);
    }

    @Test
    void shouldCreateLocalizacao() throws Exception {
        LocalizacaoRequest request = createRequest();
        LocalizacaoResponse response = createResponse();
        when(service.create(any(LocalizacaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/localizacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.locLongitude").value(-46.6333));

        verify(service, times(1)).create(any(LocalizacaoRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingLocalizacaoWithInvalidData() throws Exception {
        LocalizacaoRequest request = new LocalizacaoRequest(null, null);

        mockMvc.perform(post("/api/localizacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());

        verify(service, never()).create(any(LocalizacaoRequest.class));
    }

    @Test
    void shouldUpdateLocalizacao() throws Exception {
        LocalizacaoRequest request = createRequest();
        LocalizacaoResponse response = createResponse();
        when(service.update(eq(1L), any(LocalizacaoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/localizacoes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.locLatitude").value(-23.5505));

        verify(service, times(1)).update(eq(1L), any(LocalizacaoRequest.class));
    }

    @Test
    void shouldDeleteLocalizacao() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/localizacoes/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1L);
    }

    private LocalizacaoRequest createRequest() {
        return new LocalizacaoRequest(new BigDecimal("-23.5505"), new BigDecimal("-46.6333"));
    }

    private LocalizacaoResponse createResponse() {
        return new LocalizacaoResponse(1L, new BigDecimal("-23.5505"), new BigDecimal("-46.6333"));
    }
}
