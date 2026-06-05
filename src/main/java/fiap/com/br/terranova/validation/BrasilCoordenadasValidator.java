package fiap.com.br.terranova.validation;

import fiap.com.br.terranova.localizacao.dto.LocalizacaoRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BrasilCoordenadasValidator implements ConstraintValidator<BrasilCoordenadas, LocalizacaoRequest> {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public boolean isValid(LocalizacaoRequest request, ConstraintValidatorContext context) {
        if (request == null || request.locLatitude() == null || request.locLongitude() == null) {
            return true;
        }
        try {
            String url = String.format("https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=%s&longitude=%s",
                    request.locLatitude(), request.locLongitude());

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                // Regex para buscar countryCode: "BR" de forma segura no JSON
                if (!body.matches("(?s).*\"countryCode\"\\s*:\\s*\"BR\".*")) {
                    return false;
                }
                return true;
            }
            
            // Em caso de falha da API externa (ex: 500, timeout), retornamos true para não bloquear o usuário
            return true;

        } catch (Exception e) {
            System.err.println("Erro ao validar país via geocoding no Java: " + e.getMessage());
            return true;
        }
    }
}
