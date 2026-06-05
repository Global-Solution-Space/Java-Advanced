package fiap.com.br.terranova.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO especifico para erros de validacao simples
    public record ValidationErrorDetail(String campo, String mensagem) {
        public ValidationErrorDetail(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    // DTO padrao para respostas de erro da API
    public record ApiErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path
    ) {}

    // Captura erros de validacao de campos (@Valid / @NotBlank / @NotNull) - 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<ValidationErrorDetail>>> handleValidation(MethodArgumentNotValidException exception) {
        log.warn("Falha de validacao de entrada detectada: {} erro(s)", exception.getErrorCount());

        List<ValidationErrorDetail> errors = new ArrayList<>();

        // Adiciona erros de campos específicos
        exception.getFieldErrors().forEach(error -> errors.add(new ValidationErrorDetail(error.getField(), error.getDefaultMessage())));

        // Adiciona erros globais (nível de classe, ex: @ValidTalhaoArea)
        exception.getGlobalErrors().forEach(error -> errors.add(new ValidationErrorDetail("global", error.getDefaultMessage())));

        return ResponseEntity.badRequest().body(Map.of("erros", errors));
    }

    // Captura recursos não encontrados (ex: Pet ou Usuario não existe) - 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception, HttpServletRequest request) {

        log.warn("Recurso não encontrado: {}", exception.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Captura violacoes de regras de negocios no Service - 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception, HttpServletRequest request) {

        log.warn("Violacao de regra de negocio: {}", exception.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Captura erros de parsing do JSON (ex: enviar String em campo de Character) - 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception, HttpServletRequest request) {

        log.warn("JSON com formato ou tipos incorretos: {}", exception.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Erro na leitura dos dados. Por favor, verifique se os tipos de dados enviados no JSON estao corretos (ex: campos numericos, datas ou caracteres unicos).",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Captura erros de consistencia de dados no banco (ex: valor grande demais, chaves duplicadas) - 400 Bad Request
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception, HttpServletRequest request) {

        log.error("Erro de consistencia/integridade no banco de dados", exception);

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Erro de integridade de dados. Por favor, certifique-se de que os valores enviados não ultrapassam o limite de caracteres permitido ou violam chaves unicas.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Captura ResponseStatusException ainda usada em servicos legados - respeita o status original
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
            ResponseStatusException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        log.warn("ResponseStatusException capturada: {} - {}", status, exception.getReason());

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getReason() != null ? exception.getReason() : "Erro desconhecido.",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    // Captura NoResourceFoundException (ex: favicon.ico não encontrado) - 404 Not Found
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(
            org.springframework.web.servlet.resource.NoResourceFoundException exception, HttpServletRequest request) {

        log.warn("Recurso estatico não encontrado: {}", exception.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Captura qualquer outro erro inesperado (Fallback geral de seguranca) - 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception exception, HttpServletRequest request) {

        log.error("Erro inesperado capturado no handler global", exception);

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocorreu um erro interno inesperado no servidor. Por favor, tente novamente mais tarde.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
