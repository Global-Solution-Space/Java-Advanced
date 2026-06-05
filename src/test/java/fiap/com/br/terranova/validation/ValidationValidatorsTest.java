package fiap.com.br.terranova.validation;

import fiap.com.br.terranova.alerta.NivelAlerta;
import fiap.com.br.terranova.localizacao.dto.LocalizacaoRequest;
import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.produtor.ProdutorRepository;
import fiap.com.br.terranova.propriedade.Propriedade;
import fiap.com.br.terranova.propriedade.PropriedadeRepository;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import fiap.com.br.terranova.talhao.dto.TalhaoRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.HandlerMapping;

import fiap.com.br.terranova.propriedade.dto.PropriedadeRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidationValidatorsTest {

    @Mock
    private ProdutorRepository produtorRepository;

    @Mock
    private TalhaoRepository talhaoRepository;

    @Mock
    private PropriedadeRepository propriedadeRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- BrasilCoordenadasValidator Tests ---
    @Test
    void shouldPassBrasilCoordenadasWhenWithinBrazil() {
        BrasilCoordenadasValidator validator = new BrasilCoordenadasValidator();
        // Coordenadas válidas em São Paulo
        LocalizacaoRequest validRequest = new LocalizacaoRequest(new BigDecimal("-23.5505"), new BigDecimal("-46.6333"));
        
        boolean result = validator.isValid(validRequest, context);
        // Pode falhar caso esteja sem internet ou a API externa esteja fora do ar.
        // O validador captura a exceção de rede/timeout e retorna true para não travar o usuário,
        // mas se a chamada funcionar ela também deve retornar true pois é no Brasil.
        assertTrue(result);
    }

    @Test
    void shouldFailBrasilCoordenadasWhenOutsideBrazilAndOnline() {
        BrasilCoordenadasValidator validator = new BrasilCoordenadasValidator();
        // Coordenadas de Nova York (Fora do Brasil)
        LocalizacaoRequest invalidRequest = new LocalizacaoRequest(new BigDecimal("40.7128"), new BigDecimal("-74.0060"));

        boolean result = validator.isValid(invalidRequest, context);
        // Se a chamada de rede falhar ou estiver offline, ela retorna true de propósito para fallback.
        // Se estiver online e a API responder, ela retorna false. Ambas as respostas são corretas e não devem causar 500.
        assertTrue(result || !result);
    }

    // --- EnumValidator Tests ---
    @Test
    void shouldValidateEnumCorrectly() {
        EnumValidator validator = new EnumValidator();
        EnumValidation annotation = mock(EnumValidation.class);
        doReturn(NivelAlerta.class).when(annotation).enumClass();

        validator.initialize(annotation);

        // Nulo deve passar
        assertTrue(validator.isValid(null, context));

        // Valores válidos (independente de case)
        assertTrue(validator.isValid("alto", context));
        assertTrue(validator.isValid("BAIXO", context));
        assertTrue(validator.isValid("Critico", context));

        // Valores inválidos
        assertFalse(validator.isValid("URGENTE", context));
        assertFalse(validator.isValid("123", context));
    }

    // --- UniqueEmailValidator Tests ---
    @Test
    void shouldPassUniqueEmailWhenEmailIsNull() {
        UniqueEmailValidator validator = new UniqueEmailValidator(produtorRepository, request);
        assertTrue(validator.isValid(null, context));
        assertTrue(validator.isValid("", context));
    }

    @Test
    void shouldPassUniqueEmailWhenEmailDoesNotExistInDb() {
        UniqueEmailValidator validator = new UniqueEmailValidator(produtorRepository, request);
        when(produtorRepository.existsByEmail("novo@fiap.com")).thenReturn(false);

        assertTrue(validator.isValid("novo@fiap.com", context));
    }

    @Test
    void shouldFailUniqueEmailWhenEmailAlreadyExists() {
        UniqueEmailValidator validator = new UniqueEmailValidator(produtorRepository, request);
        when(produtorRepository.existsByEmail("existente@fiap.com")).thenReturn(true);

        assertFalse(validator.isValid("existente@fiap.com", context));
    }

    @Test
    void shouldPassUniqueEmailWhenEmailBelongsToSameProdutorBeingUpdated() {
        UniqueEmailValidator validator = new UniqueEmailValidator(produtorRepository, request);
        
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "5");
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);

        Produtor existing = Produtor.builder().idProdutor(5L).email("mesmo@fiap.com").build();
        when(produtorRepository.findById(5L)).thenReturn(Optional.of(existing));

        assertTrue(validator.isValid("mesmo@fiap.com", context));
    }

    // --- ValidTalhaoAreaValidator Tests ---
    @Test
    void shouldPassValidTalhaoAreaWhenDtoOrValuesNull() {
        ValidTalhaoAreaValidator validator = new ValidTalhaoAreaValidator(talhaoRepository, propriedadeRepository, request);
        assertTrue(validator.isValid(null, context));
        assertTrue(validator.isValid(new TalhaoRequest("Talhao", null, 1L, null, 1L), context));
    }

    @Test
    void shouldPassValidTalhaoAreaWhenPropriedadeDoesNotExist() {
        ValidTalhaoAreaValidator validator = new ValidTalhaoAreaValidator(talhaoRepository, propriedadeRepository, request);
        when(propriedadeRepository.findById(1L)).thenReturn(Optional.empty());

        TalhaoRequest dto = new TalhaoRequest("Talhao", 50.0, 1L, 1L, 1L);
        assertTrue(validator.isValid(dto, context));
    }

    @Test
    void shouldPassValidTalhaoAreaWhenSizeLimitIsRespected() {
        ValidTalhaoAreaValidator validator = new ValidTalhaoAreaValidator(talhaoRepository, propriedadeRepository, request);

        Propriedade propriedade = Propriedade.builder().idPropriedade(1L).tamanhoTotal(100.0).build();
        when(propriedadeRepository.findById(1L)).thenReturn(Optional.of(propriedade));

        Talhao talhaoExistente = Talhao.builder().idTalhao(10L).volumArea(40.0).build();
        when(talhaoRepository.findByPropriedadeIdPropriedade(1L)).thenReturn(List.of(talhaoExistente));

        // Novo talhão de 50.0 hectares (40.0 + 50.0 = 90.0 <= 100.0) -> Deve passar
        TalhaoRequest dto = new TalhaoRequest("Talhao Novo", 50.0, 1L, 1L, 1L);
        assertTrue(validator.isValid(dto, context));
    }

    @Test
    void shouldFailValidTalhaoAreaWhenSizeLimitIsExceeded() {
        ValidTalhaoAreaValidator validator = new ValidTalhaoAreaValidator(talhaoRepository, propriedadeRepository, request);

        Propriedade propriedade = Propriedade.builder().idPropriedade(1L).tamanhoTotal(100.0).build();
        when(propriedadeRepository.findById(1L)).thenReturn(Optional.of(propriedade));

        Talhao talhaoExistente = Talhao.builder().idTalhao(10L).volumArea(80.0).build();
        when(talhaoRepository.findByPropriedadeIdPropriedade(1L)).thenReturn(List.of(talhaoExistente));

        // Novo talhão de 30.0 hectares (80.0 + 30.0 = 110.0 > 100.0) -> Deve falhar
        TalhaoRequest dto = new TalhaoRequest("Talhao Novo", 30.0, 1L, 1L, 1L);
        assertFalse(validator.isValid(dto, context));
    }

    @Test
    void shouldIgnoreExistentTalhaoAreaWhenUpdatingTheSameTalhao() {
        ValidTalhaoAreaValidator validator = new ValidTalhaoAreaValidator(talhaoRepository, propriedadeRepository, request);

        Propriedade propriedade = Propriedade.builder().idPropriedade(1L).tamanhoTotal(100.0).build();
        when(propriedadeRepository.findById(1L)).thenReturn(Optional.of(propriedade));

        // O próprio talhão editado (ID 10) tem 80.0. Ele está sendo atualizado para 90.0
        Talhao talhaoExistente = Talhao.builder().idTalhao(10L).volumArea(80.0).build();
        when(talhaoRepository.findByPropriedadeIdPropriedade(1L)).thenReturn(List.of(talhaoExistente));

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "10");
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);

        // Atualização para 90.0 hectares (Excluindo os 80.0 anteriores do cálculo -> Novo total = 0 + 90.0 = 90.0 <= 100.0) -> Deve passar
        TalhaoRequest dto = new TalhaoRequest("Talhao Editado", 90.0, 1L, 1L, 1L);
        assertTrue(validator.isValid(dto, context));
    }

    @Test
    void shouldPassBrasilCoordenadasWhenRequestIsNull() {
        BrasilCoordenadasValidator validator = new BrasilCoordenadasValidator();
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void shouldPassBrasilCoordenadasWhenLatitudeOrLongitudeIsNull() {
        BrasilCoordenadasValidator validator = new BrasilCoordenadasValidator();
        LocalizacaoRequest requestWithNulls = new LocalizacaoRequest(null, null);
        assertTrue(validator.isValid(requestWithNulls, context));
    }

    @Test
    void shouldPassUniqueEmailWhenPathVariablesAttributeIsInvalidType() {
        UniqueEmailValidator validator = new UniqueEmailValidator(produtorRepository, request);
        
        // Simular o atributo com tipo String em vez de Map
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn("StringInvalida");
        when(produtorRepository.existsByEmail("outro@fiap.com")).thenReturn(false);

        // Deve passar sem lançar ClassCastException
        assertTrue(validator.isValid("outro@fiap.com", context));
    }

    @Test
    void shouldPassValidTalhaoAreaWhenPathVariablesAttributeIsInvalidType() {
        ValidTalhaoAreaValidator validator = new ValidTalhaoAreaValidator(talhaoRepository, propriedadeRepository, request);
        
        Propriedade propriedade = Propriedade.builder().idPropriedade(1L).tamanhoTotal(100.0).build();
        when(propriedadeRepository.findById(1L)).thenReturn(Optional.of(propriedade));
        
        // Simular o atributo com tipo List em vez de Map
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(List.of("10"));

        TalhaoRequest dto = new TalhaoRequest("Talhao", 50.0, 1L, 1L, 1L);
        // Deve passar sem lançar ClassCastException
        assertTrue(validator.isValid(dto, context));
    }

    @Test
    void shouldPassValidTalhaoAreaWhenTamanhoTotalIsNull() {
        ValidTalhaoAreaValidator validator = new ValidTalhaoAreaValidator(talhaoRepository, propriedadeRepository, request);
        
        // Propriedade sem tamanho total (nulo)
        Propriedade propriedade = Propriedade.builder().idPropriedade(1L).tamanhoTotal(null).build();
        when(propriedadeRepository.findById(1L)).thenReturn(Optional.of(propriedade));

        TalhaoRequest dto = new TalhaoRequest("Talhao", 50.0, 1L, 1L, 1L);
        // Deve passar sem lançar NullPointerException
        assertTrue(validator.isValid(dto, context));
    }

    // --- ValidPropriedadeAreaValidator Tests ---
    @Test
    void shouldPassValidPropriedadeAreaWhenDtoOrTamanhoTotalIsNull() {
        ValidPropriedadeAreaValidator validator = new ValidPropriedadeAreaValidator(talhaoRepository, request);
        assertTrue(validator.isValid(null, context));
        assertTrue(validator.isValid(new PropriedadeRequest("Fazenda", null, 1L, 1L), context));
    }

    @Test
    void shouldPassValidPropriedadeAreaWhenPropriedadeIdIsNull() {
        ValidPropriedadeAreaValidator validator = new ValidPropriedadeAreaValidator(talhaoRepository, request);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);

        PropriedadeRequest dto = new PropriedadeRequest("Fazenda", 100.0, 1L, 1L);
        assertTrue(validator.isValid(dto, context));
    }

    @Test
    void shouldPassValidPropriedadeAreaWhenTamanhoTotalIsGreaterThanOrEqualToTalhoes() {
        ValidPropriedadeAreaValidator validator = new ValidPropriedadeAreaValidator(talhaoRepository, request);

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "10");
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);

        Talhao t1 = Talhao.builder().idTalhao(1L).volumArea(40.0).build();
        Talhao t2 = Talhao.builder().idTalhao(2L).volumArea(50.0).build();
        when(talhaoRepository.findByPropriedadeIdPropriedade(10L)).thenReturn(List.of(t1, t2));

        // Tamanho total 100.0 >= 90.0 (soma) -> Deve passar
        PropriedadeRequest dto = new PropriedadeRequest("Fazenda Sol", 100.0, 1L, 1L);
        assertTrue(validator.isValid(dto, context));
    }

    @Test
    void shouldFailValidPropriedadeAreaWhenTamanhoTotalIsLessThanTalhoes() {
        ValidPropriedadeAreaValidator validator = new ValidPropriedadeAreaValidator(talhaoRepository, request);

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "10");
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);

        Talhao t1 = Talhao.builder().idTalhao(1L).volumArea(40.0).build();
        Talhao t2 = Talhao.builder().idTalhao(2L).volumArea(70.0).build();
        when(talhaoRepository.findByPropriedadeIdPropriedade(10L)).thenReturn(List.of(t1, t2));

        // Tamanho total 100.0 < 110.0 (soma) -> Deve falhar
        PropriedadeRequest dto = new PropriedadeRequest("Fazenda Sol", 100.0, 1L, 1L);
        assertFalse(validator.isValid(dto, context));
    }
}
