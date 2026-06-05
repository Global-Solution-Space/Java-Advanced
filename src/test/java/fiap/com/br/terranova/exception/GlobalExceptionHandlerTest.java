package fiap.com.br.terranova.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/teste");
    }

    @Test
    void shouldHandleValidationErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("produtorRequest", "nome", "nao deve estar em branco");
        ObjectError globalError = new ObjectError("localizacaoRequest", "coordenada invalida");

        when(exception.getErrorCount()).thenReturn(2);
        when(exception.getFieldErrors()).thenReturn(List.of(fieldError));
        when(exception.getGlobalErrors()).thenReturn(List.of(globalError));

        ResponseEntity<Map<String, List<GlobalExceptionHandler.ValidationErrorDetail>>> response =
                handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().get("erros").size());
        assertEquals("nome", response.getBody().get("erros").get(0).campo());
        assertEquals("nao deve estar em branco", response.getBody().get("erros").get(0).mensagem());
        assertEquals("global", response.getBody().get("erros").get(1).campo());
        assertEquals("coordenada invalida", response.getBody().get("erros").get(1).mensagem());
    }

    @Test
    void shouldHandleResourceNotFound() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleResourceNotFound(new ResourceNotFoundException("Recurso nao encontrado."), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("Recurso nao encontrado.", response.getBody().message());
        assertEquals("/api/teste", response.getBody().path());
    }

    @Test
    void shouldHandleIllegalArgument() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleIllegalArgument(new IllegalArgumentException("Regra invalida."), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Regra invalida.", response.getBody().message());
        assertEquals("/api/teste", response.getBody().path());
    }

    @Test
    void shouldHandleHttpMessageNotReadable() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleHttpMessageNotReadable(
                        new HttpMessageNotReadableException("JSON invalido", mock(HttpInputMessage.class)),
                        request
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertTrue(response.getBody().message().contains("Erro na leitura dos dados"));
        assertEquals("/api/teste", response.getBody().path());
    }

    @Test
    void shouldHandleDataIntegrityViolation() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleDataIntegrityViolation(new DataIntegrityViolationException("violacao"), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertTrue(response.getBody().message().contains("Erro de integridade de dados"));
        assertEquals("/api/teste", response.getBody().path());
    }

    @Test
    void shouldHandleResponseStatusException() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleResponseStatusException(new ResponseStatusException(HttpStatus.CONFLICT, "Conflito de dados."), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Conflict", response.getBody().error());
        assertEquals("Conflito de dados.", response.getBody().message());
        assertEquals("/api/teste", response.getBody().path());
    }

    @Test
    void shouldHandleResponseStatusExceptionWithoutReason() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleResponseStatusException(new ResponseStatusException(HttpStatus.FORBIDDEN), request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().status());
        assertEquals("Forbidden", response.getBody().error());
        assertEquals("Erro desconhecido.", response.getBody().message());
    }

    @Test
    void shouldHandleNoResourceFound() {
        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, "/favicon.ico", "Recurso nao encontrado");

        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleNoResourceFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertTrue(response.getBody().message().contains("/favicon.ico"));
        assertEquals("/api/teste", response.getBody().path());
    }

    @Test
    void shouldHandleGenericException() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleGenericException(new RuntimeException("erro interno"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals("Ocorreu um erro interno inesperado no servidor. Por favor, tente novamente mais tarde.", response.getBody().message());
        assertEquals("/api/teste", response.getBody().path());
    }
}
